package com.ssu.ongi.common.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("test")
public class MockSmsService implements SmsService {

    @Override
    public void sendVerificationCode(String to, String verificationCode) {
        log.info("[MockSMS] to: {}, code: {}", to, verificationCode);
    }
}
