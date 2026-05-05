package com.ssu.ongi.domain.medicine.dto.response;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.DeviceSlot;
import com.ssu.ongi.domain.medicine.entity.Medicine;

import java.time.LocalTime;

public record MedicineScheduleResponse(
        Long medicineId,
        String name,
        LocalTime scheduledTime,
        Integer slotNumber,
        Long deviceId
) {
    public static MedicineScheduleResponse of(Medicine medicine, DeviceSlot deviceSlot) {
        if (deviceSlot == null) {
            throw new GeneralException(ErrorStatus.DEVICE_NOT_FOUND);
        }
        return new MedicineScheduleResponse(
                medicine.getId(),
                medicine.getName(),
                medicine.getScheduledTime(),
                deviceSlot.getSlotNumber(),
                deviceSlot.getDevice().getId()
        );
    }
}
