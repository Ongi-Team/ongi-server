package com.ssu.ongi.domain.notification.event;

import java.time.LocalDateTime;

public record MedicationTakenEvent(
        Long memberId,
        Long elderId,
        Long medicineId,
        String fcmToken,
        String medicineName,
        LocalDateTime recordedAt
) {
}
