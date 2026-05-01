package com.ssu.ongi.common.jwt;

import com.ssu.ongi.domain.member.enums.LoginMode;

public record MemberPrincipal(Long memberId, LoginMode loginMode) {
}
