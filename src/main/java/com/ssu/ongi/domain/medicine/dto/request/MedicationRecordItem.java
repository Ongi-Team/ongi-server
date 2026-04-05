package com.ssu.ongi.domain.medicine.dto.request;

import com.ssu.ongi.domain.medicine.enums.MedicationResult;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MedicationRecordItem(
        @NotNull(message = "디바이스 ID는 필수입니다.")
        Long deviceId,

        @NotNull(message = "약통 번호는 필수입니다.")
        Integer dispenserSlot,

        @NotNull(message = "복약 결과는 필수입니다.")
        MedicationResult result,

        @NotNull(message = "기록 시각은 필수입니다.")
        LocalDateTime recordedAt
) {}
