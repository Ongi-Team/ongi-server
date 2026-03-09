package com.ssu.ongi.domain.member.dto.response;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.elder.dto.response.ElderResponse;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.member.enums.LoginMode;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        LoginMode loginMode,
        MemberResponse member,
        List<ElderResponse> elders,
        ElderResponse elder
) {
    public static LoginResponse of(String accessToken, String refreshToken, LoginMode loginMode, Member member) {
        if (loginMode == LoginMode.GUARDIAN) {
            return new LoginResponse(
                    accessToken, refreshToken, LoginMode.GUARDIAN,
                    MemberResponse.from(member),
                    member.getElders().stream().map(ElderResponse::from).toList(),
                    null
            );
        } else {
            return new LoginResponse(
                    accessToken, refreshToken, LoginMode.ELDER,
                    null, null,
                    member.getElders().stream()
                            .findFirst()
                            .map(ElderResponse::from)
                            .orElseThrow(() -> new GeneralException(ErrorStatus.ELDER_NOT_FOUND))
            );
        }
    }
}