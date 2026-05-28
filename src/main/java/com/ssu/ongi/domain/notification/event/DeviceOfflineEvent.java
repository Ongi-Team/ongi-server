package com.ssu.ongi.domain.notification.event;

import com.ssu.ongi.domain.device.enums.DeviceStatus;

import java.time.LocalDateTime;

public record DeviceOfflineEvent(
        Long memberId,
        Long elderId,
        Long deviceId,
        String fcmToken,
        DeviceStatus status,
        LocalDateTime lastSeenAt
) {
}
