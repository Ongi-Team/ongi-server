package com.ssu.ongi.domain.notification.service;

import com.ssu.ongi.domain.elder.repository.ElderRepository;
import com.ssu.ongi.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenCleanupService {

    private final MemberRepository memberRepository;
    private final ElderRepository elderRepository;

    /**
     * 무효 FCM 토큰을 보호자 또는 어르신 테이블에서 삭제합니다.
     * UNREGISTERED/INVALID_ARGUMENT 에러 수신 시 호출됩니다.
     */
    public void deleteByToken(String token) {
        boolean memberDeleted = deleteMemberToken(token);
        boolean elderDeleted = deleteElderToken(token);

        if (!memberDeleted && !elderDeleted) {
            log.warn("[FCM] 무효 토큰 정리 대상 없음 - token={}", token);
        }
    }

    /** 보호자 테이블에서 FCM 토큰을 삭제합니다. 삭제 성공 시 true를 반환합니다. */
    private boolean deleteMemberToken(String token) {
        return memberRepository.findByFcmToken(token)
                .map(member -> {
                    member.deleteFcmToken();
                    log.info("[FCM] 보호자 무효 토큰 삭제 - memberId={}", member.getId());
                    return true;
                })
                .orElse(false);
    }

    /** 어르신 테이블에서 FCM 토큰을 삭제합니다. 삭제 성공 시 true를 반환합니다. */
    private boolean deleteElderToken(String token) {
        return elderRepository.findByFcmToken(token)
                .map(elder -> {
                    elder.deleteFcmToken();
                    log.info("[FCM] 어르신 무효 토큰 삭제 - elderId={}", elder.getId());
                    return true;
                })
                .orElse(false);
    }
}
