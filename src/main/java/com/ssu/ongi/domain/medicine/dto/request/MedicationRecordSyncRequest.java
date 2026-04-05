package com.ssu.ongi.domain.medicine.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MedicationRecordSyncRequest(
        @Valid
        @NotNull(message = "기록 목록은 필수입니다.")
        @Size(min = 1, message = "기록은 최소 1개 이상이어야 합니다.")
        List<MedicationRecordItem> records
) {}
