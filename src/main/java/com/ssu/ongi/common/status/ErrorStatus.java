package com.ssu.ongi.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.ssu.ongi.common.base.BaseStatus;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {

    /**
     * Common
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "요청한 자원을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405", "허용되지 않은 메소드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류입니다."),
    DUPLICATE_CONSTRAINT(HttpStatus.CONFLICT, "COMMON_409", "이미 존재하는 데이터입니다."),

    /**
     * Auth
     */
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "AUTH_409", "이미 사용 중인 아이디입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_401", "아이디 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_404", "회원을 찾을 수 없습니다."),
    ELDER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_404", "어르신 정보를 찾을 수 없습니다."),
    ELDER_CANNOT_LOGOUT(HttpStatus.FORBIDDEN, "AUTH_403", "어르신 모드에서는 로그아웃이 불가능합니다."),
    ELDER_CANNOT_ACCESS(HttpStatus.FORBIDDEN, "AUTH_403", "어르신 모드에서는 사용할 수 없는 기능입니다."),

    /**
     * Phone Verification
     */
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "PHONE_400", "인증번호가 일치하지 않습니다."),
    PHONE_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "PHONE_401", "전화번호 인증이 필요합니다."),
    VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "PHONE_404", "인증번호가 만료되었거나 존재하지 않습니다."),

    /**
     * JWT
     */
    JWT_MALFORMED(HttpStatus.BAD_REQUEST, "JWT_400", "잘못된 형식의 토큰입니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT_401", "만료된 토큰입니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "JWT_401", "유효하지 않은 토큰입니다."),
    JWT_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "JWT_401", "지원하지 않는 토큰 형식입니다."),
    JWT_REFRESH_TOKEN_REUSE(HttpStatus.UNAUTHORIZED, "JWT_401", "이미 사용된 토큰입니다. 다시 로그인해주세요."),

    /**
     * Medicine
     */
    MEDICINE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEDICINE_404", "약 정보를 찾을 수 없습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_404", "복약 스케줄을 찾을 수 없습니다."),
    DUPLICATE_SCHEDULE_TIME(HttpStatus.CONFLICT, "SCHEDULE_409", "동일한 시간에 이미 스케줄이 존재합니다."),
    DEVICE_SLOT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "SCHEDULE_400", "슬롯은 최대 8개까지 등록 가능합니다."),

    /**
     * Device
     */
    DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "DEVICE_404", "디바이스를 찾을 수 없습니다."),
    DEVICE_SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "DEVICE_404", "디바이스 슬롯을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
