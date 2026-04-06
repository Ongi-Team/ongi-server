package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.medicine.dto.response.MedicationIntakeResponse;
import com.ssu.ongi.domain.medicine.repository.MedicationRecordRepository;
import com.ssu.ongi.domain.medicine.repository.MedicineScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicationRecordQueryService {

    private final MedicationRecordRepository medicationRecordRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;

    public List<MedicationIntakeResponse> getRecordsByDate(Long elderId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return medicationRecordRepository.findAllByElderIdAndRecordedAtBetween(elderId, start, end)
                .stream()
                .map(MedicationIntakeResponse::from)
                .toList();
    }

    public List<MedicationIntakeResponse> getRecordsBySchedule(Long scheduleId, Long elderId) {
        medicineScheduleRepository.findByIdAndMedicine_Elder_Id(scheduleId, elderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        return medicationRecordRepository.findAllByMedicineScheduleId(scheduleId)
                .stream()
                .map(MedicationIntakeResponse::from)
                .toList();
    }
}
