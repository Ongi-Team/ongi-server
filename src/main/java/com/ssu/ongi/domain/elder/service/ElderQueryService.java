package com.ssu.ongi.domain.elder.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.repository.ElderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ElderQueryService {

    private final ElderQueryRepository elderQueryRepository;

    public Elder getElderByIdAndMemberId(Long elderId, Long memberId) {
        return elderQueryRepository.findByIdAndMemberId(elderId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ELDER_NOT_FOUND));
    }
}
