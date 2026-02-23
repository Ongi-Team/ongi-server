package com.ssu.ongi.domain.member.controller;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.member.dto.request.FindIdRequest;
import com.ssu.ongi.domain.member.dto.request.LoginRequest;
import com.ssu.ongi.domain.member.dto.request.ResetPasswordRequest;
import com.ssu.ongi.domain.member.dto.request.SignupRequest;
import com.ssu.ongi.domain.member.dto.response.CheckIdResponse;
import com.ssu.ongi.domain.member.dto.response.FindIdResponse;
import com.ssu.ongi.domain.member.dto.response.LoginResponse;
import com.ssu.ongi.domain.member.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "보호자 정보와 어르신 정보를 함께 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        authService.signup(request);
        return ApiResponse.success(SuccessStatus.SIGNUP_SUCCESS);
    }

    @Operation(summary = "아이디 중복 확인", description = "아이디 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<CheckIdResponse>> checkLoginId(
            @RequestParam String loginId
    ) {
        CheckIdResponse response = authService.checkLoginId(loginId);
        return ApiResponse.success(SuccessStatus.OK, response);
    }

    @Operation(summary = "로그인", description = "아이디/비밀번호로 로그인하고 모드를 선택합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(SuccessStatus.LOGIN_SUCCESS, response);
    }

    @Operation(summary = "아이디 찾기", description = "전화번호로 아이디를 조회합니다.")
    @PostMapping("/find-id")
    public ResponseEntity<ApiResponse<FindIdResponse>> findId(
            @Valid @RequestBody FindIdRequest request
    ) {
        FindIdResponse response = authService.findId(request);
        return ApiResponse.success(SuccessStatus.OK, response);
    }

    @Operation(summary = "비밀번호 변경", description = "아이디와 전화번호 인증 후 비밀번호를 변경합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ApiResponse.success(SuccessStatus.PASSWORD_RESET_SUCCESS);
    }
}
