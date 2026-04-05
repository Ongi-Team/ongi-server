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
    PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, "AUTH_200_2", "비밀번호가 변경되었습니다."),

    /**
     * Medicine
     */
    SCHEDULE_SAVE_SUCCESS(HttpStatus.CREATED, "SCHEDULE_201", "복약 스케줄이 저장되었습니다."),
    SCHEDULE_READ_SUCCESS(HttpStatus.OK, "SCHEDULE_200", "복약 스케줄 조회에 성공하였습니다."),
    SCHEDULE_DELETE_SUCCESS(HttpStatus.OK, "SCHEDULE_200_2", "복약 스케줄이 삭제되었습니다."),
    RECORD_SYNC_SUCCESS(HttpStatus.OK, "RECORD_200", "복약 기록 동기화에 성공하였습니다."),
    RECORD_READ_SUCCESS(HttpStatus.OK, "RECORD_200_2", "복약 기록 조회에 성공하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
