package com.ssu.ongi.domain.elder.repository;

import com.ssu.ongi.domain.elder.entity.Elder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElderRepository extends JpaRepository<Elder, Long> {

    Optional<Elder> findByIdAndMemberId(Long elderId, Long memberId);

    Optional<Elder> findFirstByMemberId(Long memberId);

    /** FCM 토큰으로 어르신을 조회합니다. 토큰 무효화 시 사용합니다. */
    Optional<Elder> findByFcmToken(String fcmToken);
}