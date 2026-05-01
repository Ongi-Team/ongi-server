package com.ssu.ongi.domain.elder.service;

import com.ssu.ongi.domain.elder.dto.request.ElderRequest;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.member.enums.OsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ElderCommandService {

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
}
