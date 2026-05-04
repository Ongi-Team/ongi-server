package com.ssu.ongi.domain.medicine.dto.response;

import com.ssu.ongi.domain.medicine.entity.Medicine;

import java.time.LocalTime;

public record MedicineScheduleResponse(
        Long medicineId,
        String name,
        LocalTime scheduledTime
) {
    public static MedicineScheduleResponse from(Medicine medicine) {
        return new MedicineScheduleResponse(
                medicine.getId(),
                medicine.getName(),
                medicine.getScheduledTime()
        );
    }
}
