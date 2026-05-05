package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.domain.device.entity.DeviceSlot;
import com.ssu.ongi.domain.device.service.DeviceSlotQueryService;
import com.ssu.ongi.domain.elder.service.ElderQueryService;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicineQueryService {

    private final MedicineRepository medicineRepository;
    private final ElderQueryService elderQueryService;
    private final DeviceSlotQueryService deviceSlotQueryService;

    /**
     * memberId로 elder를 조회하여 복약 스케줄과 슬롯 정보를 시간 오름차순으로 반환합니다.
     */
    public List<MedicineScheduleResponse> getSchedules(Long memberId) {
        Long elderId = elderQueryService.getElderByMemberId(memberId).getId();

        List<Medicine> medicines = medicineRepository.findAllByElderIdOrderByScheduledTimeAsc(elderId);
        Map<Long, DeviceSlot> slotMap = deviceSlotQueryService.getSlotMapByElderId(elderId);

        return medicines.stream()
                .map(medicine -> MedicineScheduleResponse.of(medicine, slotMap.get(medicine.getId())))
                .toList();
    }
}
