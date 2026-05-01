package com.ssu.ongi.domain.device.dto.request;

import com.ssu.ongi.domain.device.enums.DeviceStatus;
import jakarta.validation.constraints.NotNull;

public record HeartbeatRequest(
        @NotNull DeviceStatus status,
        @NotNull Long uptimeSec,
        @NotNull Integer rssi
) {
}
