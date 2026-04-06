package com.ssu.ongi.domain.medicine.dto.response;

import java.util.List;

public record MedicineScheduleSaveResponse(
        List<MedicineScheduleResponse> schedules,
        LockTimeRangeResponse lockTimeRange
) {
}
