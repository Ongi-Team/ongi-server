package com.ssu.ongi.domain.member.dto.response;

public record CheckIdResponse(
        boolean available
) {
    public static CheckIdResponse of(boolean available) {
        return new CheckIdResponse(available);
    }
}