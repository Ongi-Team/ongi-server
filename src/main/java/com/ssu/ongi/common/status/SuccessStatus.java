package com.ssu.ongi.common.status;

import lombok.Getter;
import lombok.AllArgsConstructor;

import com.ssu.ongi.common.base.BaseStatus;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseStatus {
    // 예시
    COMMON_SUCCESS_STATUS(HttpStatus.OK, "COMMON_200", "성공적으로 처리되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
