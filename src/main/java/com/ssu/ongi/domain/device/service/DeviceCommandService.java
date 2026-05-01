package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.dto.request.DeviceRegisterRequest;
import com.ssu.ongi.domain.device.dto.request.HeartbeatRequest;
import com.ssu.ongi.domain.device.dto.response.RegisterDeviceResponse;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.member.enums.LoginMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeviceCommandService {

    private final DeviceRepository deviceRepository;
    private final ElderQueryService elderQueryService;

    public RegisterDeviceResponse registerDevice(Long memberId, LoginMode loginMode, DeviceRegisterRequest request) {
        validateGuardianOnly(loginMode);
        Elder elder = elderQueryService.getElderByMemberId(memberId);
        Device device = Device.create(elder, request.serialNumber());
        deviceRepository.save(device);
        return RegisterDeviceResponse.from(device);
    }

    public void updateHeartbeat(Long deviceId, HeartbeatRequest request) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_NOT_FOUND));
        device.updateHeartbeat(request.status(), request.uptimeSec(), request.rssi());
        log.info("[heartbeat] deviceId={} status={} rssi={} uptime={}s",
                deviceId, request.status(), request.rssi(), request.uptimeSec());
    }

    private void validateGuardianOnly(LoginMode loginMode) {
        if (loginMode == LoginMode.ELDER) {
            throw new GeneralException(ErrorStatus.ELDER_CANNOT_REGISTER_DEVICE);
        }
    }
}
