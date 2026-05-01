package com.ssu.ongi.domain.elder.repository;

import com.ssu.ongi.domain.elder.entity.Elder;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ElderQueryRepository extends Repository<Elder, Long> {
    Optional<Elder> findByIdAndMemberId(Long elderId, Long memberId);

    Optional<Elder> findFirstByMemberId(Long memberId);
}
