package com.ssu.ongi.domain.notification.event;

import com.ssu.ongi.domain.medicine.enums.MedicationResult;

import java.time.LocalDateTime;

/**
 * 복약 완료 및 미복용 알림 이벤트. result 값으로 알림 유형을 구분합니다.
 */
public record MedicationEvent(
        MedicationResult result,
        Long memberId,
        Long elderId,
        Long medicineId,
        String fcmToken,
        String medicineName,
        LocalDateTime recordedAt
) {
}
