package com.ssu.ongi.domain.device.dto.response;

import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.enums.DeviceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record DeviceStatusResponse(
        @Schema(description = "디바이스 ID")
        Long deviceId,
        @Schema(description = "디바이스 시리얼 번호")
        String serialNumber,
        @Schema(description = "디바이스 연결 상태. 최초 heartbeat 수신 전에는 null입니다.", nullable = true)
        DeviceStatus status,
        @Schema(description = "마지막 heartbeat 수신 시각. 최초 heartbeat 수신 전에는 null입니다.", nullable = true)
        LocalDateTime lastSeenAt,
        @Schema(description = "마지막 heartbeat RSSI 값. 최초 heartbeat 수신 전에는 null입니다.", nullable = true)
        Integer rssi,
        @Schema(description = "마지막 heartbeat uptime 값. 최초 heartbeat 수신 전에는 null입니다.", nullable = true)
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
