package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.mqtt.MqttPublisher;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.dto.request.DeviceRegisterRequest;
import com.ssu.ongi.domain.device.dto.response.RegisterDeviceResponse;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.medicine.service.MedicationRecordCommandService;
import com.ssu.ongi.domain.member.enums.LoginMode;
import com.ssu.ongi.domain.member.service.LoginModeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceCommandServiceTest {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-05-28T03:00:00Z"), KOREA_ZONE);

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
                mock(ApplicationEventPublisher.class),
                new LoginModeValidator(),
                FIXED_CLOCK
        );
    }

    @Test
    void serialNumber가_이미_등록되어_있으면_디바이스_등록에_실패한다() {
        Elder elder = createElder(1L);
        DeviceRegisterRequest request = new DeviceRegisterRequest("ONGI-001");

        when(elderQueryService.getElderByMemberId(1L)).thenReturn(elder);
        when(deviceRepository.existsBySerialNumberOrElderId("ONGI-001", 1L)).thenReturn(true);

        assertThatThrownBy(() -> deviceCommandService.registerDevice(1L, LoginMode.GUARDIAN, request))
                .isInstanceOf(GeneralException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.DEVICE_ALREADY_REGISTERED);

        verify(deviceRepository, never()).save(any());
    }

    @Test
    void 어르신에게_이미_등록된_디바이스가_있으면_디바이스_등록에_실패한다() {
        Elder elder = createElder(1L);
        DeviceRegisterRequest request = new DeviceRegisterRequest("ONGI-001");

        when(elderQueryService.getElderByMemberId(1L)).thenReturn(elder);
        when(deviceRepository.existsBySerialNumberOrElderId("ONGI-001", 1L)).thenReturn(true);

        assertThatThrownBy(() -> deviceCommandService.registerDevice(1L, LoginMode.GUARDIAN, request))
                .isInstanceOf(GeneralException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.DEVICE_ALREADY_REGISTERED);

        verify(deviceRepository, never()).save(any());
    }

    @Test
    void 중복이_없으면_디바이스를_등록한다() {
        Elder elder = createElder(1L);
        DeviceRegisterRequest request = new DeviceRegisterRequest("ONGI-001");

        when(elderQueryService.getElderByMemberId(1L)).thenReturn(elder);
        when(deviceRepository.existsBySerialNumberOrElderId("ONGI-001", 1L)).thenReturn(false);

        RegisterDeviceResponse response = deviceCommandService.registerDevice(1L, LoginMode.GUARDIAN, request);

        assertThat(response.deviceToken()).isNotBlank();
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void heartbeat_수신_시_Clock_기준으로_마지막_수신_시각을_저장한다() {
        Device device = Device.create(createElder(1L), "ONGI-001");
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        deviceCommandService.updateHeartbeat(1L, new com.ssu.ongi.domain.device.dto.request.HeartbeatRequest(
                com.ssu.ongi.domain.device.enums.DeviceStatus.ONLINE,
                3600L,
                -60
        ));

        assertThat(device.getLastSeenAt()).isEqualTo(LocalDateTime.now(FIXED_CLOCK));
    }

    private Elder createElder(Long id) {
        Elder elder = Elder.create("홍길동", 80, "부");
        ReflectionTestUtils.setField(elder, "id", id);
        return elder;
    }
}
