package com.ssu.ongi.domain.member.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.member.dto.request.FcmTokenRequest;
import com.ssu.ongi.domain.member.dto.request.SignupRequest;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member createMember(SignupRequest request) {
        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new GeneralException(ErrorStatus.DUPLICATE_LOGIN_ID);
        }
        return Member.create(
                request.loginId(),
                passwordEncoder.encode(request.password()),
                request.name(),
                request.phone()
        );
    }

    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    public void updatePassword(Member member, String newPassword) {
        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    public void registerFcmToken(Long memberId, FcmTokenRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        member.updateFcmToken(request.fcmToken(), request.osType());
    }
}
