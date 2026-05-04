package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.entity.DeviceSlot;
import com.ssu.ongi.domain.device.repository.DeviceSlotRepository;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeviceSlotQueryService {

    private final DeviceSlotRepository deviceSlotRepository;

    /**
     * elderId와 slotNumber로 해당 슬롯의 Medicine을 조회합니다.
     */
    public Medicine getMedicineByElderIdAndSlotNumber(Long elderId, Integer slotNumber) {
        DeviceSlot deviceSlot = deviceSlotRepository.findByElderIdAndSlotNumber(elderId, slotNumber)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));
        return deviceSlot.getMedicine();
    }
}
