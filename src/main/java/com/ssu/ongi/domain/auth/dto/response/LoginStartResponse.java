package com.ssu.ongi.domain.auth.dto.response;

public record LoginStartResponse(
        String loginSessionToken
) {
    public static LoginStartResponse from(String loginSessionToken) {
        return new LoginStartResponse(loginSessionToken);
    }
}
