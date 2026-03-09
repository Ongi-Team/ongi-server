package com.ssu.ongi.domain.member.repository;

import com.ssu.ongi.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberQueryRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByLoginIdAndPhone(String loginId, String phone);
}
