package com.ssu.ongi.common.status;

import lombok.Getter;
import lombok.AllArgsConstructor;

import com.ssu.ongi.common.base.BaseStatus;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseStatus {

    /**
     * Common
     */
    OK(HttpStatus.OK, "COMMON_200", "성공입니다."),

    /**
     * Auth
     */
    SIGNUP_SUCCESS(HttpStatus.CREATED, "AUTH_201", "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "AUTH_200", "로그인에 성공하였습니다."),
    PASSWORD_RESET_SUCCESS(HttpStatus.OK, "AUTH_200_2", "비밀번호가 변경되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
