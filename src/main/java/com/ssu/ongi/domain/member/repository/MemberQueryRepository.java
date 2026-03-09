package com.ssu.ongi.domain.member.repository;

import com.ssu.ongi.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberQueryRepository extends JpaRepository<Member, Long> {
}
