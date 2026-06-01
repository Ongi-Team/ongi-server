package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.DeviceSlot;
import com.ssu.ongi.domain.device.service.DeviceSlotQueryService;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.medicine.dto.response.DailyMedicationStatusResponse;
import com.ssu.ongi.domain.medicine.dto.response.MedicationIntakeResponse;
import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import com.ssu.ongi.domain.medicine.repository.MedicationRecordRepository;
import com.ssu.ongi.domain.medicine.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicationRecordQueryService {

    private final MedicationRecordRepository medicationRecordRepository;
    private final MedicineRepository medicineRepository;
    private final ElderQueryService elderQueryService;
    private final DeviceSlotQueryService deviceSlotQueryService;

    public List<MedicationIntakeResponse> getRecordsByDate(Long elderId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return medicationRecordRepository.findAllByElderIdAndRecordedAtBetween(elderId, start, end)
                .stream()
                .map(MedicationIntakeResponse::from)
                .toList();
    }

    public List<MedicationIntakeResponse> getRecordsByMedicine(Long medicineId, Long elderId) {
        medicineRepository.findByIdAndElderId(medicineId, elderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        return medicationRecordRepository.findAllByMedicineId(medicineId)
                .stream()
                .map(MedicationIntakeResponse::from)
                .toList();
    }

    public List<DailyMedicationStatusResponse> getDailyMedicationStatuses(Long memberId, LocalDate date) {
        Long elderId = elderQueryService.getElderByMemberId(memberId).getId();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        Map<Long, MedicationRecord> recordMap = medicationRecordRepository
                .findAllByElderIdAndRecordedAtBetween(elderId, start, end)
                .stream()
                .collect(Collectors.toMap(
                        record -> record.getMedicine().getId(),
                        Function.identity(),
                        (first, second) -> second
                ));
        Map<Long, DeviceSlot> slotMap = deviceSlotQueryService.getSlotMapByElderId(elderId);

        return medicineRepository.findAllByElderIdOrderByScheduledTimeAsc(elderId)
                .stream()
                .map(medicine -> DailyMedicationStatusResponse.of(
                        medicine, slotMap.get(medicine.getId()), recordMap.get(medicine.getId())))
                .toList();
    }
}
