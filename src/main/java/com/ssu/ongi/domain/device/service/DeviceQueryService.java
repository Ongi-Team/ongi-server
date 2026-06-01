package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.dto.response.DeviceScheduleResponse;
import com.ssu.ongi.domain.device.dto.response.DeviceStatusResponse;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.device.repository.DeviceSlotRepository;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.member.enums.LoginMode;
import com.ssu.ongi.domain.member.service.LoginModeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeviceQueryService {

    private final DeviceRepository deviceRepository;
    private final DeviceSlotRepository deviceSlotRepository;
    private final ElderQueryService elderQueryService;
    private final LoginModeValidator loginModeValidator;

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

    /**
     * 보호자의 어르신에게 등록된 디바이스 상태를 조회합니다.
     */
    public DeviceStatusResponse getDeviceStatus(Long memberId, LoginMode loginMode) {
        loginModeValidator.validateGuardianOnly(loginMode);
        Elder elder = elderQueryService.getElderByMemberId(memberId);
        Device device = getDeviceByElderId(elder.getId());
        return DeviceStatusResponse.from(device);
    }

    /**
     * 디바이스가 자신의 슬롯별 복약 시간을 조회합니다.
     */
    public List<DeviceScheduleResponse> getDeviceSchedules(Long deviceId) {
        return deviceSlotRepository.findAllWithMedicineByDeviceId(deviceId)
                .stream()
                .map(DeviceScheduleResponse::from)
                .toList();
    }
}
