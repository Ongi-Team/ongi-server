package com.ssu.ongi.domain.member.dto.response;

import com.ssu.ongi.domain.elder.dto.response.ElderResponse;
import com.ssu.ongi.domain.member.enums.LoginMode;

import java.util.List;

public record LoginResponse(
        String accessToken,
        LoginMode loginMode,
        MemberResponse member,
        List<ElderResponse> elders,
        ElderResponse elder
) {
    public static LoginResponse guardian(String accessToken, MemberResponse member, List<ElderResponse> elders) {
        return new LoginResponse(accessToken, LoginMode.GUARDIAN, member, elders, null);
    }

    public static LoginResponse elder(String accessToken, ElderResponse elder) {
        return new LoginResponse(accessToken, LoginMode.ELDER, null, null, elder);
    }
}