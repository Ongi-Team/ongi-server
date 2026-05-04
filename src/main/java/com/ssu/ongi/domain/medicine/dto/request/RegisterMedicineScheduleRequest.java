package com.ssu.ongi.domain.medicine.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegisterMedicineScheduleRequest(
        @Valid
        @NotNull(message = "스케줄 목록은 필수입니다.")
        @Size(min = 1, max = 8, message = "스케줄은 최소 1개, 최대 8개까지 등록 가능합니다.")
        List<MedicineScheduleItem> schedules
) {}
