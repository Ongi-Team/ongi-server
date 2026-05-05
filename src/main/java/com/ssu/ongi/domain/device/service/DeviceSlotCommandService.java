package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.entity.DeviceSlot;
import com.ssu.ongi.domain.device.enums.SlotStatus;
import com.ssu.ongi.domain.device.repository.DeviceSlotRepository;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DeviceSlotCommandService {

    private final DeviceSlotRepository deviceSlotRepository;

    /**
     * 해당 elder의 전체 DeviceSlot을 삭제합니다.
     */
    public void deleteAllByElderId(Long elderId) {
        deviceSlotRepository.deleteAllByElderId(elderId);
    }

    /**
     * 특정 medicine에 연관된 DeviceSlot 삭제 후 전체 재할당합니다.
     */
    public void deleteByMedicineId(Long medicineId, Long elderId) {
        deviceSlotRepository.deleteAllByMedicineId(medicineId);
        reassignSlotNumbers(elderId);
    }

    /**
     * Medicine 목록을 슬롯 1번부터 순서대로 저장합니다. (이미 정렬된 순서로 전달)
     */
    public void assignSlots(Elder elder, Device device, List<Medicine> sortedMedicines) {
        List<DeviceSlot> slots = new ArrayList<>();
        for (int i = 0; i < sortedMedicines.size(); i++) {
            slots.add(DeviceSlot.create(elder, device, sortedMedicines.get(i), i + 1));
        }
        deviceSlotRepository.saveAll(slots);
    }

    /**
     * 디바이스 ID와 슬롯 번호로 해당 슬롯의 복약 상태를 업데이트합니다.
     */
    public void updateMedicationStatus(Long deviceId, Integer slotNumber, SlotStatus status) {
        DeviceSlot deviceSlot = deviceSlotRepository.findByDeviceIdAndSlotNumber(deviceId, slotNumber)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_SLOT_NOT_FOUND));
        deviceSlot.updateStatus(status);
    }

    /**
     * 해당 elder의 전체 DeviceSlot을 scheduledTime 오름차순으로 1번부터 재할당합니다.
     */
    private void reassignSlotNumbers(Long elderId) {
        List<DeviceSlot> allSlots = deviceSlotRepository
                .findAllByElderIdOrderByMedicineScheduledTimeAsc(elderId);
        for (int i = 0; i < allSlots.size(); i++) {
            allSlots.get(i).updateSlotNumber(i + 1);
        }
    }
}
