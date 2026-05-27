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

    private static final String TOKEN_TYPE = "tokenType";
    private static final String LOGIN_SESSION_TOKEN_TYPE = "LOGIN_SESSION";

    @Value("${jwt.access-secret}")
    private String accessSecret;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${jwt.login-session-expiration:300000}")
    private long loginSessionExpiration;

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

    public String createLoginSessionToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(TOKEN_TYPE, LOGIN_SESSION_TOKEN_TYPE)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + loginSessionExpiration))
                .signWith(accessKey)
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

    public Long getMemberIdFromLoginSession(String token) {
        return Long.parseLong(getLoginSessionClaims(token).getSubject());
    }

    public LoginMode getLoginMode(String token) {
        String loginMode = getAccessClaims(token).get("loginMode", String.class);
        if (loginMode == null) {
            throw new IllegalArgumentException("loginMode claim이 없습니다.");
        }
        return LoginMode.valueOf(loginMode);
    }

    private Claims getAccessClaims(String token) {
        return Jwts.parser().verifyWith(accessKey).build()
                .parseSignedClaims(token).getPayload();
    }

    private Claims getRefreshClaims(String token) {
        return Jwts.parser().verifyWith(refreshKey).build()
                .parseSignedClaims(token).getPayload();
    }

    private Claims getLoginSessionClaims(String token) {
        Claims claims = getAccessClaims(token);
        if (!LOGIN_SESSION_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE, String.class))) {
            throw new IllegalArgumentException("유효하지 않은 로그인 세션 토큰입니다.");
        }
        return claims;
    }
}
