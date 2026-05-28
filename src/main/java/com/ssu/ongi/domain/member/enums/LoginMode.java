package com.ssu.ongi.domain.member.enums;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;

public enum LoginMode {
    GUARDIAN,
    ELDER;

    /**
     * 보호자 전용 기능에서 어르신 모드 접근을 차단합니다.
     */
    public void validateGuardianOnly() {
        if (this == ELDER) {
            throw new GeneralException(ErrorStatus.ELDER_CANNOT_ACCESS);
        }
    }
}
