package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.medicine.dto.request.MedicationRecordItem;
import com.ssu.ongi.domain.medicine.dto.request.MedicationRecordSyncRequest;
import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import com.ssu.ongi.domain.medicine.entity.MedicineSchedule;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;
import com.ssu.ongi.domain.medicine.repository.MedicationRecordRepository;
import com.ssu.ongi.domain.medicine.repository.MedicineScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicationRecordCommandService {

    private final MedicationRecordRepository medicationRecordRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;
    private final DeviceRepository deviceRepository;

    public void saveRecord(Long deviceId, Integer dispenserSlot,
                           MedicationResult result, LocalDateTime recordedAt) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_NOT_FOUND));

        Long elderId = device.getElder().getId();

        MedicineSchedule schedule = medicineScheduleRepository
                .findByElderIdAndDispenserSlot(elderId, dispenserSlot)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        // 중복 이벤트 무시 (idempotent)
        if (medicationRecordRepository.existsByMedicineScheduleIdAndRecordedAt(schedule.getId(), recordedAt)) {
            return;
        }

        MedicationRecord record = MedicationRecord.create(schedule, device, result, recordedAt);
        medicationRecordRepository.save(record);
    }

    public void syncOfflineRecords(MedicationRecordSyncRequest request) {
        for (MedicationRecordItem item : request.records()) {
            saveRecord(item.deviceId(), item.dispenserSlot(), item.result(), item.recordedAt());
        }
    }
}
