package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.mqtt.DeviceTopic;
import com.ssu.ongi.common.mqtt.MqttPublisher;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeviceCommandService {

    private final DeviceRepository deviceRepository;
    private final ElderQueryService elderQueryService;
    private final MqttPublisher mqttPublisher;

    /**
     * 보호자의 어르신에게 디바이스를 등록하고 deviceToken을 발급합니다.
     */
    public RegisterDeviceResponse registerDevice(Long memberId, LoginMode loginMode, DeviceRegisterRequest request) {
        Elder elder = getGuardianElder(memberId, loginMode);
        Device device = Device.create(elder, request.serialNumber());
        deviceRepository.save(device);
        return RegisterDeviceResponse.from(device);
    }

    /**
     * ESP32로부터 heartbeat를 수신하여 디바이스 상태를 업데이트합니다.
     */
    public void updateHeartbeat(Long deviceId, HeartbeatRequest request) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_NOT_FOUND));
        device.updateHeartbeat(request.status(), request.uptimeSec(), request.rssi());
    }

    /**
     * MQTT를 통해 디바이스에 전체 약통 열기 명령을 전달합니다.
     */
    public void openAll(Long memberId, LoginMode loginMode) {
        Elder elder = getGuardianElder(memberId, loginMode);
        Device device = deviceRepository.findByElderId(elder.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_NOT_FOUND));
        mqttPublisher.publish(DeviceTopic.openAll(device.getDeviceToken()), "OPEN_ALL");
    }

    /**
     * 보호자 모드 검증 후 어르신 정보를 반환합니다.
     */
    private Elder getGuardianElder(Long memberId, LoginMode loginMode) {
        validateGuardianOnly(loginMode);
        return elderQueryService.getElderByMemberId(memberId);
    }

    /**
     * 어르신 모드인 경우 접근을 차단합니다.
     */
    private void validateGuardianOnly(LoginMode loginMode) {
        if (loginMode == LoginMode.ELDER) {
            throw new GeneralException(ErrorStatus.ELDER_CANNOT_ACCESS);
        }
    }
}
