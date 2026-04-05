package com.ssu.ongi.domain.medicine.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MedicineScheduleCreateRequest(
        @NotNull(message = "어르신 ID는 필수입니다.")
        Long elderId,

        @Valid
        @NotNull(message = "스케줄 목록은 필수입니다.")
        List<ScheduleItem> schedules
) {}
