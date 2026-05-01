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
    LOGIN_SUCCESS(HttpStatus.OK, "AUTH_200", "로그인에 성공하였습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "AUTH_200", "로그아웃이 완료되었습니다."),
    WITHDRAW_SUCCESS(HttpStatus.OK, "AUTH_200", "회원탈퇴가 완료되었습니다."),
    PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, "AUTH_200", "비밀번호가 변경되었습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "AUTH_201", "회원가입이 완료되었습니다."),

    /**
     * Medicine Schedule
     */
    GET_MEDICINE_SCHEDULE_SUCCESS(HttpStatus.OK, "SCHEDULE_200", "복약 스케줄 조회에 성공하였습니다."),
    DELETE_MEDICINE_SCHEDULE_SUCCESS(HttpStatus.OK, "SCHEDULE_200", "복약 스케줄이 삭제되었습니다."),
    REGISTER_MEDICINE_SCHEDULE_SUCCESS(HttpStatus.CREATED, "SCHEDULE_201", "복약 스케줄이 저장되었습니다."),

    /**
     * Medication Record
     */
    SYNC_MEDICATION_RECORD_SUCCESS(HttpStatus.OK, "RECORD_200", "복약 기록 동기화에 성공하였습니다."),
    GET_MEDICATION_RECORD_SUCCESS(HttpStatus.OK, "RECORD_200", "복약 기록 조회에 성공하였습니다."),

    /**
     * Member
     */
    FCM_TOKEN_REGISTER_SUCCESS(HttpStatus.OK, "MEMBER_200", "FCM 토큰이 등록되었습니다."),
    FCM_TOKEN_DELETE_SUCCESS(HttpStatus.OK, "MEMBER_200", "FCM 토큰이 삭제되었습니다."),

    /**
     * Device
     */
    DEVICE_CONNECTION_SUCCESS(HttpStatus.OK, "DEVICE_200", "디바이스가 연결되었습니다."),
    DEVICE_REGISTER_SUCCESS(HttpStatus.CREATED, "DEVICE_201", "디바이스가 등록되었습니다."),
    DEVICE_OPEN_ALL_SUCCESS(HttpStatus.OK, "DEVICE_200", "전체 약통 열기 명령을 전달했습니다."),
    DEVICE_CLOSE_ALL_SUCCESS(HttpStatus.OK, "DEVICE_200", "전체 약통 닫기 명령을 전달했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
