package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.dto.response.DeviceScheduleResponse;
import com.ssu.ongi.domain.device.dto.response.DeviceStatusResponse;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.entity.DeviceSlot;
import com.ssu.ongi.domain.device.enums.DeviceStatus;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.device.repository.DeviceSlotRepository;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.member.enums.LoginMode;
import com.ssu.ongi.domain.member.service.LoginModeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DeviceQueryServiceTest {

    private DeviceRepository deviceRepository;
    private DeviceSlotRepository deviceSlotRepository;
    private ElderQueryService elderQueryService;
    private DeviceQueryService deviceQueryService;

    @BeforeEach
    void setUp() {
        deviceRepository = mock(DeviceRepository.class);
        deviceSlotRepository = mock(DeviceSlotRepository.class);
        elderQueryService = mock(ElderQueryService.class);
        deviceQueryService = new DeviceQueryService(
                deviceRepository, deviceSlotRepository, elderQueryService, new LoginModeValidator());
    }

    /**
     * 보호자 모드에서 등록된 디바이스 상태를 정상 조회하는지 검증합니다.
     */
    @Test
    void 디바이스_상태를_조회한다() {
        Elder elder = createElder(1L);
        Device device = createDevice(10L, elder, "ONGI-001", DeviceStatus.ONLINE, -60, 3600L);

        when(elderQueryService.getElderByMemberId(1L)).thenReturn(elder);
        when(deviceRepository.findByElderId(1L)).thenReturn(Optional.of(device));

        DeviceStatusResponse response = deviceQueryService.getDeviceStatus(1L, LoginMode.GUARDIAN);

        assertThat(response.deviceId()).isEqualTo(10L);
        assertThat(response.serialNumber()).isEqualTo("ONGI-001");
        assertThat(response.status()).isEqualTo(DeviceStatus.ONLINE);
        assertThat(response.rssi()).isEqualTo(-60);
        assertThat(response.uptimeSec()).isEqualTo(3600L);
        assertThat(response.lastSeenAt()).isNotNull();
    }

    /**
     * 최초 heartbeat 전 디바이스는 연결 상태 필드가 null로 조회되는지 검증합니다.
     */
    @Test
    void 한번도_연결되지_않은_디바이스는_null_필드로_응답한다() {
        Elder elder = createElder(1L);
        Device device = Device.create(elder, "ONGI-001");
        ReflectionTestUtils.setField(device, "id", 10L);

        when(elderQueryService.getElderByMemberId(1L)).thenReturn(elder);
        when(deviceRepository.findByElderId(1L)).thenReturn(Optional.of(device));

        DeviceStatusResponse response = deviceQueryService.getDeviceStatus(1L, LoginMode.GUARDIAN);

        assertThat(response.deviceId()).isEqualTo(10L);
        assertThat(response.serialNumber()).isEqualTo("ONGI-001");
        assertThat(response.status()).isNull();
        assertThat(response.lastSeenAt()).isNull();
        assertThat(response.rssi()).isNull();
        assertThat(response.uptimeSec()).isNull();
    }

    /**
     * 등록된 디바이스가 없으면 404 예외가 발생하는지 검증합니다.
     */
    @Test
    void 등록된_디바이스가_없으면_상태_조회에_실패한다() {
        Elder elder = createElder(1L);

        when(elderQueryService.getElderByMemberId(1L)).thenReturn(elder);
        when(deviceRepository.findByElderId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceQueryService.getDeviceStatus(1L, LoginMode.GUARDIAN))
                .isInstanceOf(GeneralException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.DEVICE_NOT_FOUND);
    }

    /**
     * 어르신 모드에서는 디바이스 상태 조회가 차단되는지 검증합니다.
     */
    @Test
    void 어르신_모드이면_상태_조회에_실패한다() {
        assertThatThrownBy(() -> deviceQueryService.getDeviceStatus(1L, LoginMode.ELDER))
                .isInstanceOf(GeneralException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.ELDER_CANNOT_ACCESS);

        verifyNoInteractions(elderQueryService);
        verifyNoInteractions(deviceRepository);
    }

    @Test
    void 디바이스_스케줄을_슬롯_번호와_시간으로_조회한다() {
        Elder elder = createElder(1L);
        Device device = createDevice(10L, elder, "ONGI-001", DeviceStatus.ONLINE, -60, 3600L);
        Medicine morningMedicine = createMedicine(100L, elder, "혈압약", LocalTime.of(8, 0));
        Medicine afternoonMedicine = createMedicine(101L, elder, "당뇨약", LocalTime.of(13, 0));
        DeviceSlot morningSlot = DeviceSlot.create(elder, device, morningMedicine, 1);
        DeviceSlot afternoonSlot = DeviceSlot.create(elder, device, afternoonMedicine, 2);

        when(deviceSlotRepository.findAllWithMedicineByDeviceId(10L))
                .thenReturn(List.of(morningSlot, afternoonSlot));

        List<DeviceScheduleResponse> response = deviceQueryService.getDeviceSchedules(10L);

        assertThat(response).hasSize(2);
        assertThat(response.get(0).slotNumber()).isEqualTo(1);
        assertThat(response.get(0).scheduledTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(response.get(1).slotNumber()).isEqualTo(2);
        assertThat(response.get(1).scheduledTime()).isEqualTo(LocalTime.of(13, 0));
    }

    /**
     * 테스트용 어르신 엔티티에 식별자를 설정합니다.
     */
    private Elder createElder(Long id) {
        Elder elder = Elder.create("홍길동", 80, "부");
        ReflectionTestUtils.setField(elder, "id", id);
        return elder;
    }

    /**
     * 테스트용 디바이스 엔티티에 상태 조회 필드를 설정합니다.
     */
    private Device createDevice(Long id, Elder elder, String serialNumber,
                                DeviceStatus status, Integer rssi, Long uptimeSec) {
        Device device = Device.create(elder, serialNumber);
        ReflectionTestUtils.setField(device, "id", id);
        ReflectionTestUtils.setField(device, "status", status);
        ReflectionTestUtils.setField(device, "rssi", rssi);
        ReflectionTestUtils.setField(device, "uptimeSec", uptimeSec);
        ReflectionTestUtils.setField(device, "lastSeenAt", LocalDateTime.now());
        return device;
    }

    private Medicine createMedicine(Long id, Elder elder, String name, LocalTime scheduledTime) {
        Medicine medicine = Medicine.create(elder, name, scheduledTime);
        ReflectionTestUtils.setField(medicine, "id", id);
        return medicine;
    }
}
