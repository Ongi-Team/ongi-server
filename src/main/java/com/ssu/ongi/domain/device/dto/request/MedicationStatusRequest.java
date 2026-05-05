package com.ssu.ongi.domain.device.dto.request;

import com.ssu.ongi.domain.device.enums.SlotStatus;
import jakarta.validation.constraints.NotNull;

public record MedicationStatusRequest(
        @NotNull(message = "슬롯 번호는 필수입니다.")
        Integer slotNumber,

        @NotNull(message = "복약 상태는 필수입니다.")
        SlotStatus status
) {}
