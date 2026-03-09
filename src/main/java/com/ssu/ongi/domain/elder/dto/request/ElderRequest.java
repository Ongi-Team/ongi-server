package com.ssu.ongi.domain.elder.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ElderRequest(
        @NotBlank(message = "어르신 성함을 입력해주세요.")
        String name,

        @NotNull(message = "어르신 연령을 입력해주세요.")
        Integer age,

        String phone,

        @NotBlank(message = "보호자와의 관계를 입력해주세요.")
        String relationship
) {
}