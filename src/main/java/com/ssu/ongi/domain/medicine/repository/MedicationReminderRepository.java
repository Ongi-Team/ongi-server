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

    /** 오늘 해당 약의 알림이 이미 발송되었는지 확인합니다. */
    public boolean isAlreadySent(Long medicineId, LocalDate date) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(buildKey(medicineId, date))
        );
    }

    /** 오늘 해당 약의 알림 발송 이력을 기록합니다. */
    public void markAsSent(Long medicineId, LocalDate date) {
        redisTemplate.opsForValue()
                .set(buildKey(medicineId, date), "sent", TTL_HOURS, TimeUnit.HOURS);
    }

    private String buildKey(Long medicineId, LocalDate date) {
        return REMINDER_PREFIX + medicineId + ":" + date;
    }
}
