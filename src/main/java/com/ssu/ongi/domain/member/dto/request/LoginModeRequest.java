package com.ssu.ongi.domain.member.dto.request;

import com.ssu.ongi.domain.member.enums.LoginMode;
import com.ssu.ongi.domain.member.enums.OsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginModeRequest(
        @NotBlank(message = "로그인 세션 토큰을 입력해주세요.")
        String loginSessionToken,

        @NotNull(message = "로그인 모드를 선택해주세요.")
        LoginMode loginMode,

        @NotBlank(message = "fcm_token을 입력해주세요.")
        @Size(max = 512, message = "fcm_token은 512자 이하여야 합니다.")
        String fcmToken,

        @NotNull(message = "OS 타입을 입력해주세요.")
        OsType osType
) {
}
