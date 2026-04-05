package com.ssu.ongi.domain.medicine.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MedicationRecordSyncRequest(
        @Valid
        @NotNull(message = "기록 목록은 필수입니다.")
        List<MedicationRecordItem> records
) {}
