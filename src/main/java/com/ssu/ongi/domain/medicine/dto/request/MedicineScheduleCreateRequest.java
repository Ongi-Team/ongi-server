package com.ssu.ongi.domain.medicine.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MedicineScheduleCreateRequest(
        @NotNull(message = "어르신 ID는 필수입니다.")
        Long elderId,

        @Valid
        @NotNull(message = "스케줄 목록은 필수입니다.")
        @Size(min = 1, message = "스케줄은 최소 1개 이상이어야 합니다.")
        List<MedicineScheduleItem> schedules
) {}
