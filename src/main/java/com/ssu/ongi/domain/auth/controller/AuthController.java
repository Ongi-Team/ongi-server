package com.ssu.ongi.domain.auth.controller;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.member.dto.request.FindIdRequest;
import com.ssu.ongi.domain.member.dto.request.LoginRequest;
import com.ssu.ongi.domain.member.dto.request.ResetPasswordRequest;
import com.ssu.ongi.domain.member.dto.request.SignupRequest;
import com.ssu.ongi.domain.member.dto.response.CheckIdResponse;
import com.ssu.ongi.domain.member.dto.response.FindIdResponse;
import com.ssu.ongi.domain.member.dto.response.LoginResponse;
import com.ssu.ongi.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        authService.signup(request);
        return ApiResponse.success(SuccessStatus.SIGNUP_SUCCESS);
    }

    @Override
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<CheckIdResponse>> checkLoginId(
            @RequestParam String loginId
    ) {
        CheckIdResponse response = authService.checkLoginId(loginId);
        return ApiResponse.success(SuccessStatus.OK, response);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(SuccessStatus.LOGIN_SUCCESS, response);
    }

    @Override
    @PostMapping("/find-id")
    public ResponseEntity<ApiResponse<FindIdResponse>> findId(
            @Valid @RequestBody FindIdRequest request
    ) {
        FindIdResponse response = authService.findId(request);
        return ApiResponse.success(SuccessStatus.OK, response);
    }

    @Override
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ApiResponse.success(SuccessStatus.PASSWORD_RESET_SUCCESS);
    }
}
