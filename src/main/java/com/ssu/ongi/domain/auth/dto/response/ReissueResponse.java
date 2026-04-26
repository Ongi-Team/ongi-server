package com.ssu.ongi.domain.auth.dto.response;

import com.ssu.ongi.common.jwt.TokenPair;

public record ReissueResponse(
        String accessToken,
        String refreshToken
) {
    public static ReissueResponse from(TokenPair tokenPair) {
        return new ReissueResponse(tokenPair.accessToken(), tokenPair.refreshToken());
    }
}
