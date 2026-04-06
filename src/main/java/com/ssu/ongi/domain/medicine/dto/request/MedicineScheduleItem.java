package com.ssu.ongi.domain.medicine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record MedicineScheduleItem(
        @NotBlank(message = "약 이름은 필수입니다.")
        String name,

        @NotNull(message = "복용 시간은 필수입니다.")
        LocalTime scheduledTime
) {}
