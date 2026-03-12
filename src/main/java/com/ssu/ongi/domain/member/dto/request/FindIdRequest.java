package com.ssu.ongi.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FindIdRequest(
        @NotBlank(message = "전화번호를 입력해주세요.")
        String phone
) {
}