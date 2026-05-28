package com.ssu.ongi.domain.notification.event;

import java.time.LocalTime;

/** 복약 시간 도래 시 보호자에게 FCM 알림을 전송하기 위한 이벤트입니다. */
public record MedicationReminderEvent(
        Long memberId,
        Long elderId,
        Long medicineId,
        String fcmToken,
        String medicineName,
        LocalTime scheduledTime
) {
}
