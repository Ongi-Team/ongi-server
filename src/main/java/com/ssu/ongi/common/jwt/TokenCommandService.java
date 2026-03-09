package com.ssu.ongi.common.jwt;

import com.ssu.ongi.domain.member.enums.LoginMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCommandService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenPair reissueTokens(Long memberId, LoginMode loginMode) {
        refreshTokenRepository.delete(memberId);

        String accessToken = jwtTokenProvider.createAccessToken(memberId, loginMode);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);
        refreshTokenRepository.save(memberId, refreshToken);

        return new TokenPair(accessToken, refreshToken);
    }
}
