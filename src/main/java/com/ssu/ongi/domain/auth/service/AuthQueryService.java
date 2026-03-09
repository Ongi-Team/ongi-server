package com.ssu.ongi.domain.auth.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.member.dto.request.FindIdRequest;
import com.ssu.ongi.domain.member.dto.response.CheckIdResponse;
import com.ssu.ongi.domain.member.dto.response.FindIdResponse;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.member.repository.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthQueryService {

    private final MemberQueryRepository memberQueryRepository;

    public CheckIdResponse checkLoginId(String loginId) {
        boolean exists = memberQueryRepository.existsByLoginId(loginId);
        return CheckIdResponse.of(!exists);
    }

    public FindIdResponse findId(FindIdRequest request) {
        Member member = memberQueryRepository.findByPhone(request.phone())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return FindIdResponse.of(member.getLoginId());
    }
}
