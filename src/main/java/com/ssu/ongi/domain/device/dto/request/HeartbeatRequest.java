package com.ssu.ongi.domain.device.dto.request;

import java.time.LocalDateTime;

import com.ssu.ongi.domain.device.enums.DeviceStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HeartbeatRequest(
	@NotBlank String serialNumber,
	@NotNull DeviceStatus status,
	@NotNull Long uptimeSec,
	@NotNull Integer rssi
) {
}
