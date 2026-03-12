package com.ssu.ongi.common.sms;

public interface SmsService {
    void sendVerificationCode(String to, String verificationCode);
}
