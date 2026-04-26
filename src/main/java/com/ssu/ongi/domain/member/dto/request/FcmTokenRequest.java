package com.ssu.ongi.domain.member.dto.request;

import com.ssu.ongi.domain.member.enums.OsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FcmTokenRequest(

        @NotBlank(message = "FCM 토큰은 필수입니다.")
        String fcmToken,

        @NotNull(message = "OS 타입은 필수입니다.")
        OsType osType
) {
}
