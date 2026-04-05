package com.ssu.ongi.domain.medicine.dto.response;

import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record MedicationIntakeResponse(
        Long recordId,
        String medicineName,
        LocalTime scheduledTime,
        MedicationResult result,
        LocalDateTime recordedAt
) {
    public static MedicationIntakeResponse from(MedicationRecord record) {
        return new MedicationIntakeResponse(
                record.getId(),
                record.getMedicineSchedule().getMedicine().getName(),
                record.getMedicineSchedule().getScheduledTime(),
                record.getResult(),
                record.getRecordedAt()
        );
    }
}
