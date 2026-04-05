package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.repository.ElderRepository;
import com.ssu.ongi.domain.medicine.dto.request.MedicineScheduleCreateRequest;
import com.ssu.ongi.domain.medicine.dto.request.ScheduleItem;
import com.ssu.ongi.domain.medicine.dto.response.LockTimeRangeResponse;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleSaveResponse;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicineScheduleCommandService {

    private final MedicineRepository medicineRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final ElderRepository elderRepository;
    private final MedicineScheduleQueryService medicineScheduleQueryService;

    public MedicineScheduleSaveResponse saveSchedules(MedicineScheduleCreateRequest request) {
        Elder elder = elderRepository.findById(request.elderId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.ELDER_NOT_FOUND));

        // scheduledTime 기준 오름차순 정렬
        List<ScheduleItem> sortedItems = request.schedules().stream()
                .sorted(Comparator.comparing(ScheduleItem::scheduledTime))
                .toList();

        // 요청 내 중복 시간 검증
        Set<LocalTime> uniqueTimes = new HashSet<>();
        for (ScheduleItem item : sortedItems) {
            if (!uniqueTimes.add(item.scheduledTime())) {
                throw new GeneralException(ErrorStatus.DUPLICATE_SCHEDULE_TIME);
            }
        }

        // 기존 DB 스케줄과 중복 시간 검증
        for (ScheduleItem item : sortedItems) {
            if (medicineScheduleRepository.existsByElderIdAndScheduledTime(elder.getId(), item.scheduledTime())) {
                throw new GeneralException(ErrorStatus.DUPLICATE_SCHEDULE_TIME);
            }
        }

        // Medicine + MedicineSchedule 생성 (dispenserSlot 자동 할당 - 기존 최대값 이후부터)
        List<MedicineSchedule> savedSchedules = new ArrayList<>();
        int slot = medicineScheduleRepository.findMaxDispenserSlotByElderId(elder.getId()) + 1;
        for (ScheduleItem item : sortedItems) {
            Medicine medicine = medicineRepository.save(Medicine.create(elder, item.name()));
            MedicineSchedule schedule = MedicineSchedule.create(medicine, slot++, item.scheduledTime());
            savedSchedules.add(medicineScheduleRepository.save(schedule));
        }

        List<MedicineScheduleResponse> scheduleResponses = savedSchedules.stream()
                .map(MedicineScheduleResponse::from)
                .toList();

        LockTimeRangeResponse lockTimeRange = medicineScheduleQueryService.calculateLockTimeRange(elder.getId());

        return new MedicineScheduleSaveResponse(scheduleResponses, lockTimeRange);
    }

    public void deleteSchedule(Long scheduleId, Long elderId) {
        MedicineSchedule schedule = medicineScheduleRepository.findByIdAndMedicine_Elder_Id(scheduleId, elderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        medicationRecordRepository.deleteAllByMedicineScheduleId(scheduleId);
        Medicine medicine = schedule.getMedicine();
        medicineScheduleRepository.delete(schedule);
        medicineRepository.delete(medicine);
    }
}
