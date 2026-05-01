package com.ssu.ongi.domain.auth.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.jwt.TokenCommandService;
import com.ssu.ongi.common.jwt.TokenPair;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.auth.dto.response.ReissueResponse;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderCommandService;
import com.ssu.ongi.domain.member.dto.request.LoginRequest;
import com.ssu.ongi.domain.member.dto.request.ReissueRequest;
import com.ssu.ongi.domain.member.dto.request.SignupRequest;
import com.ssu.ongi.domain.member.dto.request.UpdatePasswordRequest;
import com.ssu.ongi.domain.auth.dto.response.LoginResponse;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.member.enums.LoginMode;
import com.ssu.ongi.domain.member.service.MemberCommandService;
import com.ssu.ongi.domain.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthCommandService {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final ElderCommandService elderCommandService;
    private final TokenCommandService tokenCommandService;
    private final PhoneVerificationService phoneVerificationService;

    public void signup(SignupRequest request) {
        phoneVerificationService.validateVerified(request.phone());

        Member member = memberCommandService.createMember(request);
        Elder elder = elderCommandService.createElder(request.elder());
        member.addElder(elder);
        memberCommandService.saveMember(member);
    }

    public LoginResponse login(LoginRequest request) {
        Member member = memberQueryService.findByLoginIdWithElders(request.loginId());
        memberQueryService.validatePassword(member, request.password());

        TokenPair tokens = tokenCommandService.issueTokens(member.getId(), request.loginMode());

        if (request.loginMode() == LoginMode.GUARDIAN) {
            memberCommandService.updateFcmToken(member, request.fcmToken(), request.osType());
        } else {
            Elder elder = member.getElders().stream()
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorStatus.ELDER_NOT_FOUND));
            elderCommandService.updateFcmToken(elder, request.fcmToken(),request.osType());
        }

        return LoginResponse.of(tokens.accessToken(), tokens.refreshToken(), request.loginMode(), member);
    }

    public void logout(Long memberId) {
        tokenCommandService.logout(memberId);
        memberCommandService.deleteFcmToken(memberId);
    }

    public void withdraw(Long memberId) {
        tokenCommandService.logout(memberId);
        memberCommandService.withdraw(memberId);
    }

    public void updatePassword(UpdatePasswordRequest request) {
        phoneVerificationService.validateVerified(request.phone());

        Member member = memberQueryService.findByLoginIdAndPhone(request.loginId(), request.phone());
        memberCommandService.updatePassword(member, request.newPassword());
    }

    public ReissueResponse reissue(ReissueRequest request) {
        Long memberId = tokenCommandService.getMemberIdFromRefreshToken(request.refreshToken());
        TokenPair tokens = tokenCommandService.reissueTokens(memberId, request.refreshToken(), request.loginMode());
        return ReissueResponse.from(tokens);
    }
}
