package com.ssu.ongi.common.jwt;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.member.enums.LoginMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCommandService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public Long getMemberIdFromRefreshToken(String refreshToken) {
        return jwtTokenProvider.getMemberIdFromRefresh(refreshToken);
    }

    // 로그인 시 호출 - 검증 없이 바로 발급 (기존 토큰 무효화 후 신규 발급)
    public TokenPair issueTokens(Long memberId, LoginMode loginMode) {
        refreshTokenRepository.delete(memberId);
        return generateAndSave(memberId, loginMode);
    }

    // 재발급 시 호출 - Redis에 저장된 토큰과 대조 후 발급 (RTR 핵심)
    public TokenPair reissueTokens(Long memberId, String incomingRefreshToken, LoginMode loginMode) {
        String stored = refreshTokenRepository.find(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.JWT_EXPIRED));

        if (!stored.equals(incomingRefreshToken)) {
            // 저장된 토큰과 다르면 탈취 의심 → 강제 로그아웃
            refreshTokenRepository.delete(memberId);
            throw new GeneralException(ErrorStatus.JWT_REFRESH_TOKEN_REUSE);
        }

        refreshTokenRepository.delete(memberId);
        return generateAndSave(memberId, loginMode);
    }

    // 로그아웃 시 호출 - Redis의 RefreshToken 삭제
    public void logout(Long memberId) {
        refreshTokenRepository.delete(memberId);
    }

    private TokenPair generateAndSave(Long memberId, LoginMode loginMode) {
        String accessToken = jwtTokenProvider.createAccessToken(memberId, loginMode);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);
        refreshTokenRepository.save(memberId, refreshToken);
        return new TokenPair(accessToken, refreshToken);
    }
}
