package com.ssu.ongi.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ongi.device.offline")
public record DeviceOfflineProperties(
        long temporaryThresholdMinutes,
        long longThresholdMinutes,
        long checkIntervalMs
) {
}
