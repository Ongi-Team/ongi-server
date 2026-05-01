package com.ssu.ongi.common.device;

import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeviceTokenValidator {

    private final DeviceRepository deviceRepository;

    public Optional<Device> validate(String deviceToken) {
        return deviceRepository.findByDeviceToken(deviceToken);
    }
}
