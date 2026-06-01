package com.ssu.ongi.domain.medicine.dto.response;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.DeviceSlot;
import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record DailyMedicationStatusResponse(
        Long medicineId,
        String medicineName,
        LocalTime scheduledTime,
        Integer slotNumber,
        Long deviceId,
        boolean taken,
        MedicationResult result,
        LocalDateTime recordedAt
) {
    public static DailyMedicationStatusResponse of(Medicine medicine, DeviceSlot deviceSlot, MedicationRecord record) {
        if (deviceSlot == null) {
            throw new GeneralException(ErrorStatus.DEVICE_NOT_FOUND);
        }

        MedicationResult result = record == null ? null : record.getResult();
        LocalDateTime recordedAt = record == null ? null : record.getRecordedAt();

        return new DailyMedicationStatusResponse(
                medicine.getId(),
                medicine.getName(),
                medicine.getScheduledTime(),
                deviceSlot.getSlotNumber(),
                deviceSlot.getDevice().getId(),
                result == MedicationResult.TAKEN,
                result,
                recordedAt
        );
    }
}
