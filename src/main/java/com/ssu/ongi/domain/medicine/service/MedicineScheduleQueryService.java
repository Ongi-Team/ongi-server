package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.domain.medicine.dto.response.LockTimeRangeResponse;
import java.time.LocalTime;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import com.ssu.ongi.domain.medicine.entity.MedicineSchedule;
import com.ssu.ongi.domain.medicine.repository.MedicineScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicineScheduleQueryService {

    private final MedicineScheduleRepository medicineScheduleRepository;

    public List<MedicineScheduleResponse> getSchedules(Long elderId) {
        return medicineScheduleRepository.findAllByElderIdWithMedicine(elderId)
                .stream()
                .map(MedicineScheduleResponse::from)
                .toList();
    }

    public LockTimeRangeResponse calculateLockTimeRange(Long elderId) {
        List<MedicineSchedule> schedules =
                medicineScheduleRepository.findAllByElderIdWithMedicine(elderId);

        if (schedules.isEmpty()) {
            return null;
        }

        var earliest = schedules.get(0).getScheduledTime();
        var latest = schedules.get(schedules.size() - 1).getScheduledTime();

        var lockStart = safeMinusMinutes(earliest, 30);
        return new LockTimeRangeResponse(lockStart, latest);
    }

    private LocalTime safeMinusMinutes(LocalTime time, int minutes) {
        var result = time.minusMinutes(minutes);
        return result.isAfter(time) ? LocalTime.MIN : result;
    }
}
