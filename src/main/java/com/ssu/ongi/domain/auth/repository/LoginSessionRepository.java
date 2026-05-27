package com.ssu.ongi.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class LoginSessionRepository {

    private static final String PREFIX = "login-session:";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${auth.login-session-expiration:300000}")
    private long loginSessionExpiration;

    public void save(String loginSessionToken, Long memberId) {
        redisTemplate.opsForValue()
                .set(PREFIX + loginSessionToken, String.valueOf(memberId), loginSessionExpiration, TimeUnit.MILLISECONDS);
    }

    public Optional<Long> consume(String loginSessionToken) {
        return Optional.ofNullable(redisTemplate.opsForValue().getAndDelete(PREFIX + loginSessionToken))
                .map(Long::valueOf);
    }
}
