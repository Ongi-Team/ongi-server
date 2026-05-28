package com.ssu.ongi.domain.medicine.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * 복약 시간 알림 중복 발송 방지를 위한 Redis 저장소입니다.
 * 약(medicineId)과 날짜(date) 조합으로 당일 알림 발송 여부를 관리합니다.
 */
@Repository
@RequiredArgsConstructor
public class MedicationReminderRepository {

    private static final String REMINDER_PREFIX = "fcm:reminder:";
    private static final long TTL_HOURS = 25L;  // 하루 지나면 자동 만료 (여유 1시간)

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 발송 이력이 없을 때만 원자적으로 기록하고 true를 반환합니다.
     * SET NX를 사용해 분산 환경에서의 중복 발송 race condition을 방지합니다.
     * 이미 발송된 경우 false를 반환합니다.
     */
    public boolean markAsSentIfAbsent(Long medicineId, LocalDate date) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(buildKey(medicineId, date), "sent", TTL_HOURS, TimeUnit.HOURS);
        return Boolean.TRUE.equals(success);
    }

    private String buildKey(Long medicineId, LocalDate date) {
        return REMINDER_PREFIX + medicineId + ":" + date;
    }
}
