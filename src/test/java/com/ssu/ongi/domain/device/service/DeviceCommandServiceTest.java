package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.mqtt.MqttPublisher;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.dto.request.DeviceRegisterRequest;
import com.ssu.ongi.domain.device.dto.request.HeartbeatRequest;
import com.ssu.ongi.domain.device.dto.request.MedicationStatusRequest;
import com.ssu.ongi.domain.device.dto.response.RegisterDeviceResponse;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.entity.DeviceSlot;
import com.ssu.ongi.domain.device.enums.DeviceStatus;
import com.ssu.ongi.domain.device.enums.SlotStatus;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;
import com.ssu.ongi.domain.medicine.service.MedicationRecordCommandService;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.member.enums.LoginMode;
import com.ssu.ongi.domain.member.service.LoginModeValidator;
import com.ssu.ongi.domain.notification.event.MedicationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceCommandServiceTest {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-05-28T03:00:00Z"), KOREA_ZONE);

    private DeviceRepository deviceRepository;
    private ElderQueryService elderQueryService;
    private DeviceSlotCommandService deviceSlotCommandService;
    private MedicationRecordCommandService medicationRecordCommandService;
    private ApplicationEventPublisher eventPublisher;
    private DeviceCommandService deviceCommandService;

    @BeforeEach
    void setUp() {
        deviceRepository = mock(DeviceRepository.class);
        elderQueryService = mock(ElderQueryService.class);
        deviceSlotCommandService = mock(DeviceSlotCommandService.class);
        medicationRecordCommandService = mock(MedicationRecordCommandService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        deviceCommandService = new DeviceCommandService(
                deviceRepository,
                elderQueryService,
                mock(MqttPublisher.class),
                deviceSlotCommandService,
                medicationRecordCommandService,
                eventPublisher,
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

        deviceCommandService.updateHeartbeat(1L, new HeartbeatRequest(DeviceStatus.ONLINE, 3600L, -60));

        assertThat(device.getLastSeenAt()).isEqualTo(LocalDateTime.now(FIXED_CLOCK));
    }

    @Test
    void MISSED_상태_수신_시_미복용_기록을_저장하고_알림_이벤트를_발행한다() {
        DeviceSlot slot = createDeviceSlot();
        MedicationRecord record = mock(MedicationRecord.class);

        when(deviceSlotCommandService.updateMedicationStatus(1L, 1, SlotStatus.MISSED)).thenReturn(slot);
        when(medicationRecordCommandService.saveMedicationIntake(
                eq(slot), eq(MedicationResult.MISSED), any())).thenReturn(Optional.of(record));

        deviceCommandService.updateMedicationStatus(1L, new MedicationStatusRequest(1, SlotStatus.MISSED));

        ArgumentCaptor<MedicationEvent> captor = ArgumentCaptor.forClass(MedicationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        MedicationEvent event = captor.getValue();
        assertThat(event.result()).isEqualTo(MedicationResult.MISSED);
        assertThat(event.medicineName()).isEqualTo("혈압약");
        assertThat(event.recordedAt()).isEqualTo(LocalDateTime.now(FIXED_CLOCK));
    }

    @Test
    void MISSED_기록이_중복이면_알림_이벤트를_발행하지_않는다() {
        DeviceSlot slot = createDeviceSlot();

        when(deviceSlotCommandService.updateMedicationStatus(1L, 1, SlotStatus.MISSED)).thenReturn(slot);
        when(medicationRecordCommandService.saveMedicationIntake(
                eq(slot), eq(MedicationResult.MISSED), any())).thenReturn(Optional.empty());

        deviceCommandService.updateMedicationStatus(1L, new MedicationStatusRequest(1, SlotStatus.MISSED));

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void TAKEN_상태_수신_시_복약_기록을_저장하고_알림_이벤트를_발행한다() {
        DeviceSlot slot = createDeviceSlot();
        MedicationRecord record = mock(MedicationRecord.class);

        when(deviceSlotCommandService.updateMedicationStatus(1L, 1, SlotStatus.TAKEN)).thenReturn(slot);
        when(medicationRecordCommandService.saveMedicationIntake(
                eq(slot), eq(MedicationResult.TAKEN), any())).thenReturn(Optional.of(record));

        deviceCommandService.updateMedicationStatus(1L, new MedicationStatusRequest(1, SlotStatus.TAKEN));

        ArgumentCaptor<MedicationEvent> captor = ArgumentCaptor.forClass(MedicationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().result()).isEqualTo(MedicationResult.TAKEN);
    }

    /** 테스트용 어르신 엔티티에 id를 설정합니다. */
    private Elder createElder(Long id) {
        Elder elder = Elder.create("홍길동", 80, "부");
        ReflectionTestUtils.setField(elder, "id", id);
        return elder;
    }

    /** 테스트용 DeviceSlot을 보호자·어르신·약 관계까지 포함해 생성합니다. */
    private DeviceSlot createDeviceSlot() {
        Member member = Member.create("guardian", "encoded-pw", "보호자", "01000000000");
        ReflectionTestUtils.setField(member, "id", 10L);
        ReflectionTestUtils.setField(member, "fcmToken", "fcm-token");

        Elder elder = Elder.create("어르신", 80, "부");
        ReflectionTestUtils.setField(elder, "id", 20L);
        member.addElder(elder);

        Device device = Device.create(elder, "ONGI-001");
        ReflectionTestUtils.setField(device, "id", 1L);

        Medicine medicine = Medicine.create(elder, "혈압약", LocalTime.of(8, 0));
        ReflectionTestUtils.setField(medicine, "id", 100L);

        DeviceSlot slot = DeviceSlot.create(elder, device, medicine, 1);
        ReflectionTestUtils.setField(slot, "id", 1L);
        return slot;
    }
}
