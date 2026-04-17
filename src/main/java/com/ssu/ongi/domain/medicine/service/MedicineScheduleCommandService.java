package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.repository.ElderRepository;
import com.ssu.ongi.domain.medicine.dto.request.RegisterMedicineScheduleRequest;
import com.ssu.ongi.domain.medicine.dto.request.MedicineScheduleItem;
import com.ssu.ongi.domain.medicine.dto.response.LockTimeRangeResponse;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.entity.MedicineSchedule;
import com.ssu.ongi.domain.medicine.repository.MedicationRecordRepository;
import com.ssu.ongi.domain.medicine.repository.MedicineRepository;
import com.ssu.ongi.domain.medicine.repository.MedicineScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicineScheduleCommandService {

    private final MedicineRepository medicineRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final ElderRepository elderRepository;
    private final MedicineScheduleQueryService medicineScheduleQueryService;


    /**
     * 어르신 검증
     * 정렬 중복 검증
     * 약 등록
     * */
    public void saveSchedules(
            Long memberId,
            RegisterMedicineScheduleRequest request
    ) {
        Elder elder = elderRepository.findByIdAndMemberId(request.elderId(), memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ELDER_NOT_FOUND));

        List<MedicineScheduleItem> sortedItems = validateAndSort(request.schedules(), elder.getId());
        List<MedicineSchedule> savedSchedules = createAndSaveSchedules(elder, sortedItems);

        // TODO 디바이스 스케줄에 직접 저장하는 코드를 호출해야 함
        LockTimeRangeResponse lockTimeRange = medicineScheduleQueryService.calculateLockTimeRange(savedSchedules);
    }

    private List<MedicineScheduleItem> validateAndSort(List<MedicineScheduleItem> items, Long elderId) {
        List<MedicineScheduleItem> sortedItems = items.stream()
                .sorted(Comparator.comparing(MedicineScheduleItem::scheduledTime))
                .toList();

        // 요청 내 중복 시간 검증
        Set<LocalTime> requestTimes = sortedItems.stream()
                .map(MedicineScheduleItem::scheduledTime)
                .collect(Collectors.toSet());
        if (requestTimes.size() != sortedItems.size()) {
            throw new GeneralException(ErrorStatus.DUPLICATE_SCHEDULE_TIME);
        }

        // 기존 DB 스케줄과 중복 시간 검증
        for (MedicineScheduleItem item : sortedItems) {
            if (medicineScheduleRepository.existsByElderIdAndScheduledTime(elderId, item.scheduledTime())) {
                throw new GeneralException(ErrorStatus.DUPLICATE_SCHEDULE_TIME);
            }
        }

        return sortedItems;
    }

    private List<MedicineSchedule> createAndSaveSchedules(Elder elder, List<MedicineScheduleItem> sortedItems) {
        // Medicine 일괄 저장 (배치 INSERT)
        List<Medicine> medicines = sortedItems.stream()
                .map(item -> Medicine.create(elder, item.name()))
                .toList();
        medicineRepository.saveAll(medicines);

        // MedicineSchedule 일괄 저장 (dispenserSlot 자동 할당 - 기존 최대값 이후부터)
        int slot = medicineScheduleRepository.findMaxDispenserSlotByElderId(elder.getId()) + 1;
        List<MedicineSchedule> schedules = new ArrayList<>();
        for (int i = 0; i < sortedItems.size(); i++) {
            schedules.add(MedicineSchedule.create(medicines.get(i), slot++, sortedItems.get(i).scheduledTime()));
        }

        return medicineScheduleRepository.saveAll(schedules);
    }

    public void deleteSchedule(Long scheduleId, Long elderId) {
        MedicineSchedule schedule = medicineScheduleRepository.findByIdAndMedicine_Elder_Id(scheduleId, elderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        medicationRecordRepository.deleteAllByMedicineScheduleId(scheduleId);
        Medicine medicine = schedule.getMedicine();
        medicineScheduleRepository.delete(schedule);

        if (!medicineScheduleRepository.existsByMedicineId(medicine.getId())) {
            medicineRepository.delete(medicine);
        }
    }
}
