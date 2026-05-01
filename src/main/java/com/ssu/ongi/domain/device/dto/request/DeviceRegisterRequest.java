package com.ssu.ongi.domain.device.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeviceRegisterRequest(
        @NotBlank(message = "시리얼 번호는 필수입니다.")
        String serialNumber
) {
}
