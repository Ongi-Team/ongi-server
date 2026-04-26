package com.ssu.ongi.domain.auth.dto.response;

public record CheckIdResponse(
        boolean available
) {
    public static CheckIdResponse of(boolean available) {
        return new CheckIdResponse(available);
    }
}