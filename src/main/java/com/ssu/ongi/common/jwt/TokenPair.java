package com.ssu.ongi.common.jwt;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
