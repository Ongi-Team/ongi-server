package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.mqtt.MqttPublisher;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.dto.request.DeviceRegisterRequest;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.medicine.service.MedicationRecordCommandService;
import com.ssu.ongi.domain.member.enums.LoginMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceCommandServiceTest {

    private DeviceRepository deviceRepository;
    private ElderQueryService elderQueryService;
    private DeviceCommandService deviceCommandService;

    @BeforeEach
    void setUp() {
        deviceRepository = mock(DeviceRepository.class);
        elderQueryService = mock(ElderQueryService.class);

        deviceCommandService = new DeviceCommandService(
                deviceRepository,
                elderQueryService,
                mock(MqttPublisher.class),
                mock(DeviceSlotCommandService.class),
                mock(MedicationRecordCommandService.class),
                mock(ApplicationEventPublisher.class)
        );
    }

    @Test
    void serialNumber가_이미_등록되어_있으면_디바이스_등록에_실패한다() {
        Elder elder = createElder(1L);
        DeviceRegisterRequest request = new DeviceRegisterRequest("ONGI-001");

        when(elderQueryService.getElderByMemberId(1L)).thenReturn(elder);
        when(deviceRepository.existsBySerialNumber("ONGI-001")).thenReturn(true);

        assertThatThrownBy(() -> deviceCommandService.registerDevice(1L, LoginMode.GUARDIAN, request))
                .isInstanceOf(GeneralException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.DEVICE_ALREADY_REGISTERED);

        verify(deviceRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 어르신에게_이미_등록된_디바이스가_있으면_디바이스_등록에_실패한다() {
        Elder elder = createElder(1L);
        DeviceRegisterRequest request = new DeviceRegisterRequest("ONGI-001");

        when(elderQueryService.getElderByMemberId(1L)).thenReturn(elder);
        when(deviceRepository.existsBySerialNumber("ONGI-001")).thenReturn(false);
        when(deviceRepository.existsByElderId(1L)).thenReturn(true);

        assertThatThrownBy(() -> deviceCommandService.registerDevice(1L, LoginMode.GUARDIAN, request))
                .isInstanceOf(GeneralException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.DEVICE_ALREADY_REGISTERED);

        verify(deviceRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private Elder createElder(Long id) {
        Elder elder = Elder.create("홍길동", 80, "부");
        ReflectionTestUtils.setField(elder, "id", id);
        return elder;
    }
}
