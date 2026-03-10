package com.ssu.ongi.common.sms;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"dev", "prod"})
public class CoolSmsService implements SmsService {

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.from}")
    private String from;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    @Override
    public void sendVerificationCode(String to, String verificationCode) {
        log.info("[CoolSMS] 발송 시도 - to: {}, code: {}", to, verificationCode);
        try {
            Message message = new Message();
            message.setFrom(from);
            message.setTo(to);
            message.setText("[ONGI] 인증번호 [" + verificationCode + "]를 입력해주세요. (3분 이내)");
            messageService.sendOne(new SingleMessageSendingRequest(message));
            log.info("[CoolSMS] 발송 성공 - to: {}", to);
        } catch (Exception e) {
            log.error("[CoolSMS] 발송 실패 - to: {}, error: {}", to, e.getMessage(), e);
            throw e;
        }
    }
}
