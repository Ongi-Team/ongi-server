package com.ssu.ongi.domain.member.dto.request;

import com.ssu.ongi.domain.member.enums.LoginMode;
import com.ssu.ongi.domain.member.enums.OsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "아이디를 입력해주세요.")
        String loginId,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,

        @NotNull(message = "로그인 모드를 선택해주세요.")
        LoginMode loginMode,

        @NotBlank(message = "fcm_token을 입력해주세요.")
        @Size(max = 512, message = "fcm_token은 512자 이하여야 합니다.")
        String fcmToken,

        @NotNull(message = "OS 타입을 입력해주세요.")
        OsType osType
) {
}