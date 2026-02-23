package com.ssu.ongi.domain.member.dto.response;

import com.ssu.ongi.domain.member.entity.Member;

public record MemberResponse(
        Long memberId,
        String name,
        String phone
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getPhone()
        );
    }
}