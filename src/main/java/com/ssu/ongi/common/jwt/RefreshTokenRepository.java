package com.ssu.ongi.common.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String PREFIX = "refresh:";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public void save(Long memberId, String refreshToken) {
        redisTemplate.opsForValue()
                .set(PREFIX + memberId, refreshToken, refreshExpiration, TimeUnit.MILLISECONDS);
    }

    public Optional<String> find(Long memberId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(PREFIX + memberId));
    }

    public void delete(Long memberId) {
        redisTemplate.delete(PREFIX + memberId);
    }
}
