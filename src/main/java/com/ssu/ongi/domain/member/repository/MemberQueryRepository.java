package com.ssu.ongi.domain.member.repository;

import com.ssu.ongi.domain.member.entity.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberQueryRepository extends Repository<Member, Long> {

    @Query("SELECT m FROM Member m JOIN FETCH m.elders WHERE m.loginId = :loginId")
    Optional<Member> findByLoginIdWithElders(@Param("loginId") String loginId);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findById(Long id);

    boolean existsByLoginId(String loginId);

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByLoginIdAndPhone(String loginId, String phone);
}
