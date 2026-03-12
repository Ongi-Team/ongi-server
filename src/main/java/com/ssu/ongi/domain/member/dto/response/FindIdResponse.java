package com.ssu.ongi.domain.member.dto.response;

public record FindIdResponse(
        String loginId
) {
    public static FindIdResponse of(String loginId) {
        return new FindIdResponse(loginId);
    }
}