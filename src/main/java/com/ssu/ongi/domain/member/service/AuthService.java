package com.ssu.ongi.domain.member.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.jwt.JwtTokenProvider;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.elder.dto.response.ElderResponse;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.member.dto.request.FindIdRequest;
import com.ssu.ongi.domain.member.dto.request.LoginRequest;
import com.ssu.ongi.domain.member.dto.request.ResetPasswordRequest;
import com.ssu.ongi.domain.member.dto.request.SignupRequest;
import com.ssu.ongi.domain.member.dto.response.CheckIdResponse;
import com.ssu.ongi.domain.member.dto.response.FindIdResponse;
import com.ssu.ongi.domain.member.dto.response.LoginResponse;
import com.ssu.ongi.domain.member.dto.response.MemberResponse;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.member.enums.LoginMode;
import com.ssu.ongi.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signup(SignupRequest request) {
        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new GeneralException(ErrorStatus.DUPLICATE_LOGIN_ID);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = request.toEntity(encodedPassword);
        Elder elder = request.elder().toEntity();

        member.addElder(elder);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public CheckIdResponse checkLoginId(String loginId) {
        boolean exists = memberRepository.existsByLoginId(loginId);
        return CheckIdResponse.of(!exists);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createToken(member.getId(), request.loginMode());

        if (request.loginMode() == LoginMode.GUARDIAN) {
            MemberResponse memberResponse = MemberResponse.from(member);
            List<ElderResponse> elderResponses = member.getElders().stream()
                    .map(ElderResponse::from)
                    .toList();
            return LoginResponse.guardian(accessToken, memberResponse, elderResponses);
        } else {
            Elder elder = member.getElders().stream()
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorStatus.ELDER_NOT_FOUND));
            return LoginResponse.elder(accessToken, ElderResponse.from(elder));
        }
    }

    @Transactional(readOnly = true)
    public FindIdResponse findId(FindIdRequest request) {
        Member member = memberRepository.findByPhone(request.phone())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return FindIdResponse.of(member.getLoginId());
    }

    public void resetPassword(ResetPasswordRequest request) {
        Member member = memberRepository.findByLoginIdAndPhone(request.loginId(), request.phone())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(request.newPassword());
        member.updatePassword(encodedPassword);
    }
}
