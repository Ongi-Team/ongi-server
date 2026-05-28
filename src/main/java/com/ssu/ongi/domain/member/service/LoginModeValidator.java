package com.ssu.ongi.domain.member.service;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.member.enums.LoginMode;
import org.springframework.stereotype.Component;

@Component
public class LoginModeValidator {

    /**
     * 보호자 전용 기능에서 어르신 모드 접근을 차단합니다.
     */
    public void validateGuardianOnly(LoginMode loginMode) {
        if (loginMode == LoginMode.ELDER) {
            throw new GeneralException(ErrorStatus.ELDER_CANNOT_ACCESS);
        }
    }
}
