package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.elder.repository.ElderRepository;
import com.ssu.ongi.domain.medicine.dto.response.LockTimeRangeResponse;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicineQueryService {

    private final ElderRepository elderRepository;
    private final MedicineRepository medicineRepository;

    public List<MedicineScheduleResponse> getSchedules(Long memberId, Long elderId) {
        elderRepository.findByIdAndMemberId(elderId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ELDER_NOT_FOUND));

        return medicineRepository.findAllByElderIdOrderByScheduledTimeAsc(elderId)
                .stream()
                .map(MedicineScheduleResponse::from)
                .toList();
    }

    public LockTimeRangeResponse calculateLockTimeRange(List<Medicine> medicines) {
        if (medicines.isEmpty()) {
            return null;
        }

        LocalTime earliest = medicines.get(0).getScheduledTime();
        LocalTime latest = medicines.get(medicines.size() - 1).getScheduledTime();
        LocalTime lockStart = safeMinusMinutes(earliest, 30);

        return new LockTimeRangeResponse(lockStart, latest);
    }

    private LocalTime safeMinusMinutes(LocalTime time, int minutes) {
        LocalTime result = time.minusMinutes(minutes);
        return result.isAfter(time) ? LocalTime.MIN : result;
    }
}
