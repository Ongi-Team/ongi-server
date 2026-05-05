package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.device.service.DeviceSlotQueryService;
import com.ssu.ongi.domain.medicine.dto.request.MedicationIntakeItem;
import com.ssu.ongi.domain.medicine.dto.request.MedicationRecordSyncRequest;
import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;
import com.ssu.ongi.domain.medicine.repository.MedicationRecordRepository;
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
    private final DeviceRepository deviceRepository;
    private final DeviceSlotQueryService deviceSlotQueryService;

    /**
     * 디바이스로부터 단건 복약 이벤트를 수신하여 저장합니다.
     */
    public void saveMedicationIntake(Long deviceId, Integer slotNumber,
                                     MedicationResult result, LocalDateTime recordedAt) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_NOT_FOUND));

        Long elderId = device.getElder().getId();
        Medicine medicine = deviceSlotQueryService.getMedicineByElderIdAndSlotNumber(elderId, slotNumber);

        if (medicationRecordRepository.existsByMedicineIdAndRecordedAt(medicine.getId(), recordedAt)) {
            return;
        }

        medicationRecordRepository.save(MedicationRecord.create(medicine, device, result, recordedAt));
    }

    /**
     * 디바이스 재연결 시 오프라인 기록을 일괄 동기화합니다.
     */
    public void saveMedicationIntakes(MedicationRecordSyncRequest request) {
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
            Medicine medicine = deviceSlotQueryService.getMedicineByElderIdAndSlotNumber(elderId, item.dispenserSlot());

            if (medicationRecordRepository.existsByMedicineIdAndRecordedAt(medicine.getId(), item.recordedAt())) {
                continue;
            }

            toSave.add(MedicationRecord.create(medicine, device, item.result(), item.recordedAt()));
        }

        if (!toSave.isEmpty()) {
            medicationRecordRepository.saveAll(toSave);
        }
    }
}
