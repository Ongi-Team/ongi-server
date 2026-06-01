package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.entity.DeviceSlot;
import com.ssu.ongi.domain.device.service.DeviceSlotQueryService;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.medicine.dto.response.DailyMedicationStatusResponse;
import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;
import com.ssu.ongi.domain.medicine.repository.MedicationRecordRepository;
import com.ssu.ongi.domain.medicine.repository.MedicineRepository;
import com.ssu.ongi.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MedicationRecordQueryServiceTest {

    private MedicationRecordRepository medicationRecordRepository;
    private MedicineRepository medicineRepository;
    private ElderQueryService elderQueryService;
    private DeviceSlotQueryService deviceSlotQueryService;
    private MedicationRecordQueryService medicationRecordQueryService;

    @BeforeEach
    void setUp() {
        medicationRecordRepository = mock(MedicationRecordRepository.class);
        medicineRepository = mock(MedicineRepository.class);
        elderQueryService = mock(ElderQueryService.class);
        deviceSlotQueryService = mock(DeviceSlotQueryService.class);
        medicationRecordQueryService = new MedicationRecordQueryService(
                medicationRecordRepository, medicineRepository, elderQueryService, deviceSlotQueryService);
    }

    @Test
    void 날짜별_복약_상태는_전체_스케줄에_해당_날짜_기록을_붙여서_반환한다() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        Elder elder = createElder(10L);
        Medicine takenMedicine = createMedicine(1L, elder, "혈압약", LocalTime.of(8, 0));
        Medicine pendingMedicine = createMedicine(2L, elder, "당뇨약", LocalTime.of(13, 0));
        Device device = Device.create(elder, "serial-number");
        ReflectionTestUtils.setField(device, "id", 30L);
        DeviceSlot takenSlot = DeviceSlot.create(elder, device, takenMedicine, 1);
        DeviceSlot pendingSlot = DeviceSlot.create(elder, device, pendingMedicine, 2);
        MedicationRecord record = MedicationRecord.create(
                takenMedicine, device, MedicationResult.TAKEN, LocalDateTime.of(2026, 6, 1, 8, 3));

        when(elderQueryService.getElderByMemberId(100L)).thenReturn(elder);
        when(medicineRepository.findAllByElderIdOrderByScheduledTimeAsc(10L))
                .thenReturn(List.of(takenMedicine, pendingMedicine));
        when(medicationRecordRepository.findAllByElderIdAndRecordedAtBetween(
                10L, date.atStartOfDay(), date.atTime(LocalTime.MAX)))
                .thenReturn(List.of(record));
        when(deviceSlotQueryService.getSlotMapByElderId(10L))
                .thenReturn(Map.of(1L, takenSlot, 2L, pendingSlot));

        List<DailyMedicationStatusResponse> result =
                medicationRecordQueryService.getDailyMedicationStatuses(100L, date);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).medicineId()).isEqualTo(1L);
        assertThat(result.get(0).medicineName()).isEqualTo("혈압약");
        assertThat(result.get(0).slotNumber()).isEqualTo(1);
        assertThat(result.get(0).deviceId()).isEqualTo(30L);
        assertThat(result.get(0).taken()).isTrue();
        assertThat(result.get(0).result()).isEqualTo(MedicationResult.TAKEN);
        assertThat(result.get(0).recordedAt()).isEqualTo(LocalDateTime.of(2026, 6, 1, 8, 3));

        assertThat(result.get(1).medicineId()).isEqualTo(2L);
        assertThat(result.get(1).medicineName()).isEqualTo("당뇨약");
        assertThat(result.get(1).slotNumber()).isEqualTo(2);
        assertThat(result.get(1).deviceId()).isEqualTo(30L);
        assertThat(result.get(1).taken()).isFalse();
        assertThat(result.get(1).result()).isNull();
        assertThat(result.get(1).recordedAt()).isNull();
    }

    @Test
    void 당일_복약_기록이_없어도_전체_스케줄을_미복용_상태로_반환한다() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        Elder elder = createElder(10L);
        Medicine morningMedicine = createMedicine(1L, elder, "혈압약", LocalTime.of(8, 0));
        Medicine afternoonMedicine = createMedicine(2L, elder, "당뇨약", LocalTime.of(13, 0));
        Device device = Device.create(elder, "serial-number");
        ReflectionTestUtils.setField(device, "id", 30L);

        when(elderQueryService.getElderByMemberId(100L)).thenReturn(elder);
        when(medicineRepository.findAllByElderIdOrderByScheduledTimeAsc(10L))
                .thenReturn(List.of(morningMedicine, afternoonMedicine));
        when(medicationRecordRepository.findAllByElderIdAndRecordedAtBetween(
                10L, date.atStartOfDay(), date.atTime(LocalTime.MAX)))
                .thenReturn(List.of());
        when(deviceSlotQueryService.getSlotMapByElderId(10L))
                .thenReturn(Map.of(
                        1L, DeviceSlot.create(elder, device, morningMedicine, 1),
                        2L, DeviceSlot.create(elder, device, afternoonMedicine, 2)
                ));

        List<DailyMedicationStatusResponse> result =
                medicationRecordQueryService.getDailyMedicationStatuses(100L, date);

        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(response -> {
            assertThat(response.taken()).isFalse();
            assertThat(response.result()).isNull();
            assertThat(response.recordedAt()).isNull();
        });
    }

    @Test
    void 스케줄에_연결된_디바이스_슬롯이_없으면_예외가_발생한다() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        Elder elder = createElder(10L);
        Medicine medicine = createMedicine(1L, elder, "혈압약", LocalTime.of(8, 0));

        when(elderQueryService.getElderByMemberId(100L)).thenReturn(elder);
        when(medicineRepository.findAllByElderIdOrderByScheduledTimeAsc(10L))
                .thenReturn(List.of(medicine));
        when(medicationRecordRepository.findAllByElderIdAndRecordedAtBetween(
                10L, date.atStartOfDay(), date.atTime(LocalTime.MAX)))
                .thenReturn(List.of());
        when(deviceSlotQueryService.getSlotMapByElderId(10L)).thenReturn(Map.of());

        assertThatThrownBy(() -> medicationRecordQueryService.getDailyMedicationStatuses(100L, date))
                .isInstanceOf(GeneralException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.DEVICE_NOT_FOUND);
    }

    private Elder createElder(Long id) {
        Member member = Member.create("guardian", "encoded-pw", "보호자", "01000000000");
        Elder elder = Elder.create("어르신", 80, "부");
        member.addElder(elder);
        ReflectionTestUtils.setField(elder, "id", id);
        return elder;
    }

    private Medicine createMedicine(Long id, Elder elder, String name, LocalTime scheduledTime) {
        Medicine medicine = Medicine.create(elder, name, scheduledTime);
        ReflectionTestUtils.setField(medicine, "id", id);
        return medicine;
    }
}
