package com.ssu.ongi.domain.elder.repository;

import com.ssu.ongi.domain.elder.entity.Elder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElderRepository extends JpaRepository<Elder, Long> {

    Optional<Elder> findByIdAndMemberId(Long elderId, Long memberId);

    Optional<Elder> findFirstByMemberId(Long memberId);
}