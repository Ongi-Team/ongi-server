package com.ssu.ongi.domain.member.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.member.repository.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberQueryRepository memberQueryRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인 전용 조회 - 아이디 없음도 INVALID_CREDENTIALS로 통일 (타이밍 어택 방지)
    public Member findByLoginId(String loginId) {
        return memberQueryRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_CREDENTIALS));
    }

    public Member findByLoginIdAndPhone(String loginId, String phone) {
        return memberQueryRepository.findByLoginIdAndPhone(loginId, phone)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    // 비밀번호 불일치도 INVALID_CREDENTIALS로 통일 (타이밍 어택 방지)
    public void validatePassword(Member member, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_CREDENTIALS);
        }
    }
}
