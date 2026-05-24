package com.ssu.ongi.common.fcm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("test")
public class MockFcmService implements FcmService {

    @Override
    public String send(FcmMessage fcmMessage) {
        log.info("[MockFCM] 발송 생략 - title: {}, body: {}", fcmMessage.title(), fcmMessage.body());
        return "mock-fcm-message-id";
    }
}
