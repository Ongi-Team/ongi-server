package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.medicine.dto.request.MedicationIntakeItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicationRecordCommandService {

    private final MedicationRecordRepository medicationRecordRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;
    private final DeviceRepository deviceRepository;

    public void saveMedicationIntake(Long deviceId, Integer dispenserSlot,
                                    MedicationResult result, LocalDateTime recordedAt) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_NOT_FOUND));

        Long elderId = device.getElder().getId();

        MedicineSchedule schedule = medicineScheduleRepository
                .findByElderIdAndDispenserSlot(elderId, dispenserSlot)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        if (medicationRecordRepository.existsByMedicineScheduleIdAndRecordedAt(schedule.getId(), recordedAt)) {
            return;
        }

        MedicationRecord record = MedicationRecord.create(schedule, device, result, recordedAt);
        medicationRecordRepository.save(record);
    }

    public void saveMedicationIntakes(MedicationRecordSyncRequest request) {
        // 필요한 Device 일괄 조회
        Set<Long> deviceIds = request.records().stream()
                .map(MedicationIntakeItem::deviceId)
                .collect(Collectors.toSet());
        Map<Long, Device> deviceMap = deviceRepository.findAllById(deviceIds).stream()
                .collect(Collectors.toMap(Device::getId, d -> d));

        List<MedicationRecord> toSave = new ArrayList<>();

        for (MedicationIntakeItem item : request.records()) {
            Device device = deviceMap.get(item.deviceId());
            if (device == null) {
                throw new GeneralException(ErrorStatus.DEVICE_NOT_FOUND);
            }

            Long elderId = device.getElder().getId();
            MedicineSchedule schedule = medicineScheduleRepository
                    .findByElderIdAndDispenserSlot(elderId, item.dispenserSlot())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

            if (medicationRecordRepository.existsByMedicineScheduleIdAndRecordedAt(schedule.getId(), item.recordedAt())) {
                continue;
            }

            toSave.add(MedicationRecord.create(schedule, device, item.result(), item.recordedAt()));
        }

        if (!toSave.isEmpty()) {
            medicationRecordRepository.saveAll(toSave);
        }
    }
}
