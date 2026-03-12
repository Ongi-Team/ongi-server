package com.ssu.ongi.domain.member.dto.request;

import com.ssu.ongi.domain.member.enums.LoginMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReissueRequest(
        @NotBlank(message = "리프레시 토큰을 입력해주세요.")
        String refreshToken,

        @NotNull(message = "로그인 모드를 선택해주세요.")
        LoginMode loginMode
) {
}
