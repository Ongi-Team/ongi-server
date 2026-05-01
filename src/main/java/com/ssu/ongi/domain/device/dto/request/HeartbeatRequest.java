package com.ssu.ongi.domain.device.dto.request;

import com.ssu.ongi.domain.device.enums.DeviceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record HeartbeatRequest(
        @NotNull DeviceStatus status,
        @NotNull @PositiveOrZero Long uptimeSe,
        @NotNull Integer rssi
) {
}
