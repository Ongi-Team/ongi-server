package com.ssu.ongi.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class PhoneVerificationRepository {

    private static final String CODE_PREFIX = "sms:code:";
    private static final String VERIFIED_PREFIX = "sms:verified:";
    private static final long CODE_TTL = 3L;       // 인증번호 유효시간 3분
    private static final long VERIFIED_TTL = 10L;  // 인증 완료 유효시간 10분

    private final RedisTemplate<String, String> redisTemplate;

    public void saveCode(String phone, String code) {
        redisTemplate.opsForValue()
                .set(CODE_PREFIX + phone, code, CODE_TTL, TimeUnit.MINUTES);
    }

    public Optional<String> findCode(String phone) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(CODE_PREFIX + phone));
    }

    public void deleteCode(String phone) {
        redisTemplate.delete(CODE_PREFIX + phone);
    }

    public void saveVerified(String phone) {
        redisTemplate.opsForValue()
                .set(VERIFIED_PREFIX + phone, "true", VERIFIED_TTL, TimeUnit.MINUTES);
    }

    public boolean isVerified(String phone) {
        return Boolean.TRUE.toString().equals(
                redisTemplate.opsForValue().get(VERIFIED_PREFIX + phone)
        );
    }

    public void deleteVerified(String phone) {
        redisTemplate.delete(VERIFIED_PREFIX + phone);
    }
}
