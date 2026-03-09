package com.ssu.ongi.domain.auth.service;

import com.ssu.ongi.common.jwt.TokenCommandService;
import com.ssu.ongi.common.jwt.TokenPair;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.service.ElderCommandService;
import com.ssu.ongi.domain.member.dto.request.LoginRequest;
import com.ssu.ongi.domain.member.dto.request.SignupRequest;
import com.ssu.ongi.domain.member.dto.request.UpdatePasswordRequest;
import com.ssu.ongi.domain.member.dto.response.LoginResponse;
import com.ssu.ongi.domain.member.entity.Member;
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

    public void signup(SignupRequest request) {
        Member member = memberCommandService.createMember(request);
        Elder elder = elderCommandService.createElder(request.elder());

        member.addElder(elder);
        memberCommandService.saveMember(member);
    }

    public LoginResponse login(LoginRequest request) {
        Member member = memberQueryService.findByLoginId(request.loginId());
        memberQueryService.validatePassword(member, request.password());

        TokenPair tokens = tokenCommandService.reissueTokens(member.getId(), request.loginMode());
        return LoginResponse.of(tokens.accessToken(), tokens.refreshToken(), request.loginMode(), member);
    }

    public void updatePassword(UpdatePasswordRequest request) {
        Member member = memberQueryService.findByLoginIdAndPhone(request.loginId(), request.phone());
        memberCommandService.updatePassword(member, request.newPassword());
    }
}
