package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeviceQueryService {

    private final DeviceRepository deviceRepository;

    /**
     * deviceToken으로 디바이스를 조회합니다. (ESP32 인증 시 사용)
     */
    public Device getDeviceByToken(String deviceToken) {
        return deviceRepository.findByDeviceToken(deviceToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_NOT_FOUND));
    }

    /**
     * elderId로 등록된 디바이스를 조회합니다.
     */
    public Device getDeviceByElderId(Long elderId) {
        return deviceRepository.findByElderId(elderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_NOT_FOUND));
    }
}
