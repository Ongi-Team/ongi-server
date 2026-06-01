package com.ssu.ongi.domain.device.dto.response;

import com.ssu.ongi.domain.device.entity.DeviceSlot;

import java.time.LocalTime;

public record DeviceScheduleResponse(
        Integer slotNumber,
        LocalTime scheduledTime
) {
    public static DeviceScheduleResponse from(DeviceSlot deviceSlot) {
        return new DeviceScheduleResponse(
                deviceSlot.getSlotNumber(),
                deviceSlot.getMedicine().getScheduledTime()
        );
    }
}
