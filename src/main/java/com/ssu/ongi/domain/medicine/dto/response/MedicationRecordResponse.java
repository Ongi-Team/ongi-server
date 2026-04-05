package com.ssu.ongi.domain.medicine.dto.response;

import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record MedicationRecordResponse(
        Long recordId,
        String medicineName,
        LocalTime scheduledTime,
        MedicationResult result,
        LocalDateTime recordedAt
) {
    public static MedicationRecordResponse from(MedicationRecord record) {
        return new MedicationRecordResponse(
                record.getId(),
                record.getMedicineSchedule().getMedicine().getName(),
                record.getMedicineSchedule().getScheduledTime(),
                record.getResult(),
                record.getRecordedAt()
        );
    }
}
