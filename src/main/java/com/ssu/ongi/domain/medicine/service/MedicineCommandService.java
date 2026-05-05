package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.mqtt.DeviceTopic;
import com.ssu.ongi.common.mqtt.MqttPublisher;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.service.DeviceQueryService;
import com.ssu.ongi.domain.device.service.DeviceSlotCommandService;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.medicine.dto.request.MedicineScheduleItem;
import com.ssu.ongi.domain.medicine.dto.request.RegisterMedicineScheduleRequest;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.repository.MedicationRecordRepository;
import com.ssu.ongi.domain.medicine.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicineCommandService {

    private static final int MAX_SLOT_COUNT = 8;

    private final MedicineRepository medicineRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final DeviceQueryService deviceQueryService;
    private final DeviceSlotCommandService deviceSlotCommandService;
    private final ElderQueryService elderQueryService;
    private final MqttPublisher mqttPublisher;

    /**
     * 기존 스케줄 전체 삭제 후 새 스케줄 등록, 슬롯 할당, 디바이스에 업데이트 알림 전송
     */
    public void registerSchedules(Long memberId, RegisterMedicineScheduleRequest request) {
        Elder elder = elderQueryService.getElderByMemberId(memberId);
        Device device = deviceQueryService.getDeviceByElderId(elder.getId());

        List<MedicineScheduleItem> sortedItems = validateAndSort(request.schedules());

        clearAllSchedules(elder.getId());

        List<Medicine> savedMedicines = createAndSaveMedicines(elder, sortedItems);
        deviceSlotCommandService.assignSlots(elder, device, savedMedicines);
        mqttPublisher.publish(DeviceTopic.scheduleUpdated(device.getDeviceToken()), "SCHEDULE_UPDATED");
    }

    /**
     * 복약 스케줄 단건 삭제 — 연관된 복약 기록, DeviceSlot 삭제 후 슬롯 재정렬
     */
    public void deleteSchedule(Long memberId, Long medicineId) {
        Elder elder = elderQueryService.getElderByMemberId(memberId);
        Medicine medicine = medicineRepository.findByIdAndElderId(medicineId, elder.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        medicationRecordRepository.deleteAllByMedicineId(medicine.getId());
        deviceSlotCommandService.deleteByMedicineId(medicineId, elder.getId());
        medicineRepository.delete(medicine);
    }

    /**
     * 요청 스케줄을 시간 오름차순 정렬 후 요청 내 중복 시간 및 8개 초과 검증합니다.
     */
    private List<MedicineScheduleItem> validateAndSort(List<MedicineScheduleItem> items) {
        if (items.size() > MAX_SLOT_COUNT) {
            throw new GeneralException(ErrorStatus.DEVICE_SLOT_LIMIT_EXCEEDED);
        }

        List<MedicineScheduleItem> sortedItems = items.stream()
                .sorted(Comparator.comparing(MedicineScheduleItem::scheduledTime))
                .toList();

        Set<LocalTime> uniqueTimes = sortedItems.stream()
                .map(MedicineScheduleItem::scheduledTime)
                .collect(Collectors.toSet());
        if (uniqueTimes.size() != sortedItems.size()) {
            throw new GeneralException(ErrorStatus.DUPLICATE_SCHEDULE_TIME);
        }

        return sortedItems;
    }

    /**
     * 해당 elder의 기존 복약 기록, DeviceSlot, Medicine을 전체 삭제합니다.
     */
    private void clearAllSchedules(Long elderId) {
        medicationRecordRepository.deleteAllByElderId(elderId);
        deviceSlotCommandService.deleteAllByElderId(elderId);
        medicineRepository.deleteAllByElderId(elderId);
    }

    /**
     * Medicine을 생성하여 일괄 저장합니다.
     */
    private List<Medicine> createAndSaveMedicines(Elder elder, List<MedicineScheduleItem> sortedItems) {
        List<Medicine> medicines = sortedItems.stream()
                .map(item -> Medicine.create(elder, item.name(), item.scheduledTime()))
                .toList();
        return medicineRepository.saveAll(medicines);
    }
}
