package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.elder.repository.ElderRepository;
import com.ssu.ongi.domain.medicine.dto.response.LockTimeRangeResponse;
import java.time.LocalTime;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import com.ssu.ongi.domain.medicine.entity.MedicineSchedule;
import com.ssu.ongi.domain.medicine.repository.MedicineScheduleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicineScheduleQueryService {

    private final ElderRepository elderRepository;
    private final MedicineScheduleQueryRepository medicineScheduleQueryRepository;

    public List<MedicineScheduleResponse> getSchedules(Long memberId, Long elderId) {
        elderRepository.findByIdAndMemberId(elderId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ELDER_NOT_FOUND));

        return medicineScheduleQueryRepository.findAllByElderId(elderId)
                .stream()
                .map(MedicineScheduleResponse::from)
                .toList();
    }

    public LockTimeRangeResponse calculateLockTimeRange(List<MedicineSchedule> schedules) {
        if (schedules.isEmpty()) {
            return null;
        }

        LocalTime earliest = schedules.get(0).getScheduledTime();
        LocalTime latest = schedules.get(schedules.size() - 1).getScheduledTime();
        LocalTime lockStart = safeMinusMinutes(earliest, 30);

        return new LockTimeRangeResponse(lockStart, latest);
    }

    private LocalTime safeMinusMinutes(LocalTime time, int minutes) {
        LocalTime result = time.minusMinutes(minutes);
        return result.isAfter(time) ? LocalTime.MIN : result;
    }
}
