package com.ssu.ongi.domain.auth.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.sms.SmsService;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.auth.dto.request.SendVerificationRequest;
import com.ssu.ongi.domain.auth.dto.request.VerifyCodeRequest;
import com.ssu.ongi.domain.auth.repository.PhoneVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

    private final PhoneVerificationRepository phoneVerificationRepository;
    private final SmsService smsService;

    private static final SecureRandom RANDOM = new SecureRandom();

    // 인증 코드 생성 및 전송
    public void sendVerificationCode(SendVerificationRequest request) {
        String code = generateCode();
        phoneVerificationRepository.saveCode(request.phone(), code);
        smsService.sendVerificationCode(request.phone(), code);
    }

    // 인증 코드 검증
    public void verifyCode(VerifyCodeRequest request) {
        String stored = phoneVerificationRepository.findCode(request.phone())
                .orElseThrow(() -> new GeneralException(ErrorStatus.VERIFICATION_CODE_NOT_FOUND));

        if (!stored.equals(request.code())) {
            throw new GeneralException(ErrorStatus.VERIFICATION_CODE_MISMATCH);
        }

        // 인증 성공 → 코드 삭제 후 인증 완료 표시
        phoneVerificationRepository.deleteCode(request.phone());
        phoneVerificationRepository.saveVerified(request.phone());
    }

    // 인증 완료 여부 검사
    public void validateVerified(String phone) {
        if (!phoneVerificationRepository.isVerified(phone)) {
            throw new GeneralException(ErrorStatus.PHONE_NOT_VERIFIED);
        }
    }

    // 6자리 코드 생성
    private String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
