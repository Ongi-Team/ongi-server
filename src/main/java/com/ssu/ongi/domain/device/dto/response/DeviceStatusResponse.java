package com.ssu.ongi.domain.device.dto.response;

import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.enums.DeviceStatus;

import java.time.LocalDateTime;

public record DeviceStatusResponse(
        Long deviceId,
        String serialNumber,
        DeviceStatus status,
        LocalDateTime lastSeenAt,
        Integer rssi,
        Long uptimeSec
) {
    /**
     * 디바이스 엔티티를 상태 조회 응답으로 변환합니다.
     */
    public static DeviceStatusResponse from(Device device) {
        return new DeviceStatusResponse(
                device.getId(),
                device.getSerialNumber(),
                device.getStatus(),
                device.getLastSeenAt(),
                device.getRssi(),
                device.getUptimeSec()
        );
    }
}
