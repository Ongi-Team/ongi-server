package com.ssu.ongi.domain.medicine.dto.response;

import com.ssu.ongi.domain.medicine.entity.MedicineSchedule;

import java.time.LocalTime;

public record MedicineScheduleResponse(
        Long scheduleId,
        Long medicineId,
        String name,
        Integer dispenserSlot,
        LocalTime scheduledTime
) {
    public static MedicineScheduleResponse from(MedicineSchedule schedule) {
        return new MedicineScheduleResponse(
                schedule.getId(),
                schedule.getMedicine().getId(),
                schedule.getMedicine().getName(),
                schedule.getDispenserSlot(),
                schedule.getScheduledTime()
        );
    }
}
