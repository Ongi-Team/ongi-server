package com.ssu.ongi.domain.elder.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.elder.dto.request.ElderRequest;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.repository.ElderRepository;
import com.ssu.ongi.domain.member.enums.OsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ElderCommandService {

    private final ElderRepository elderRepository;

    public Elder createElder(ElderRequest request) {
        return Elder.create(
                request.name(),
                request.age(),
                request.phone(),
                request.relationship()
        );
    }

    public void updateFcmToken(Elder elder, String fcmToken, OsType osType) {
        elder.updateFcmToken(fcmToken, osType);
    }

    public void deleteFcmToken(Long memberId) {
        elderRepository.findFirstByMemberId(memberId)
                .ifPresent(Elder::deleteFcmToken);
    }
}
