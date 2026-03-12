package com.ssu.ongi.common.jwt;

import com.ssu.ongi.domain.member.enums.LoginMode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.access-secret}")
    private String accessSecret;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey accessKey;
    private SecretKey refreshKey;

    @PostConstruct
    protected void init() {
        this.accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(accessSecret));
        this.refreshKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(refreshSecret));
    }

    public String createAccessToken(Long memberId, LoginMode loginMode) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("loginMode", loginMode.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(accessKey)
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpiration))
                .signWith(refreshKey)
                .compact();
    }

    // AccessToken에서 memberId 추출 (Filter에서 사용)
    public Long getMemberId(String token) {
        return Long.parseLong(getAccessClaims(token).getSubject());
    }

    // RefreshToken에서 memberId 추출 (재발급 시 사용)
    public Long getMemberIdFromRefresh(String token) {
        return Long.parseLong(getRefreshClaims(token).getSubject());
    }

    public LoginMode getLoginMode(String token) {
        return LoginMode.valueOf(getAccessClaims(token).get("loginMode", String.class));
    }

    private Claims getAccessClaims(String token) {
        return Jwts.parser().verifyWith(accessKey).build()
                .parseSignedClaims(token).getPayload();
    }

    private Claims getRefreshClaims(String token) {
        return Jwts.parser().verifyWith(refreshKey).build()
                .parseSignedClaims(token).getPayload();
    }
}
