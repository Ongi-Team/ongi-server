package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.properties.DeviceOfflineProperties;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.enums.DeviceStatus;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceOfflineMonitorService {

    private final DeviceRepository deviceRepository;
    private final DeviceOfflineProperties properties;
    private final Clock clock;

    /**
     * 주기적으로 디바이스 heartbeat 수신 시각을 기준으로 오프라인 상태를 갱신합니다.
     */
    @Scheduled(fixedDelayString = "${ongi.device.offline.check-interval-ms:60000}")
    @Transactional
    public void monitorOfflineDevices() {
        updateOfflineStatuses();
    }

    /**
     * 전체 디바이스의 lastSeenAt을 기준으로 오프라인 상태를 판별합니다.
     */
    public void updateOfflineStatuses() {
        LocalDateTime now = LocalDateTime.now(clock);
        deviceRepository.findAllWithElderAndMember()
                .forEach(device -> resolveOfflineStatus(device, now)
                        .ifPresent(device::updateStatus));
    }

    /**
     * 마지막 heartbeat 이후 경과 시간으로 변경할 오프라인 상태를 계산합니다.
     */
    private Optional<DeviceStatus> resolveOfflineStatus(Device device, LocalDateTime now) {
        if (device.getLastSeenAt() == null) {
            return Optional.empty();
        }

        Duration elapsed = Duration.between(device.getLastSeenAt(), now);
        if (elapsed.toMinutes() >= properties.longThresholdMinutes()) {
            return Optional.of(DeviceStatus.LONG_OFFLINE);
        }
        if (elapsed.toMinutes() >= properties.temporaryThresholdMinutes()) {
            return Optional.of(DeviceStatus.TEMP_OFFLINE);
        }
        return Optional.empty();
    }
}
