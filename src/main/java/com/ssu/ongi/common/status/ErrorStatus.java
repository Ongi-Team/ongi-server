package com.ssu.ongi.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.ssu.ongi.common.base.BaseStatus;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {
    // 예시
    COMMON_ERROR_STATUS(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
