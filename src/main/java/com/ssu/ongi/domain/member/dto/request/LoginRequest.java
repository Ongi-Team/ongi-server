package com.ssu.ongi.domain.member.dto.request;

import com.ssu.ongi.domain.member.enums.LoginMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank(message = "아이디를 입력해주세요.")
        String loginId,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,

        @NotNull(message = "로그인 모드를 선택해주세요.")
        LoginMode loginMode
) {
}