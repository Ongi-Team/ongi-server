package com.ssu.ongi.domain.auth.controller;

import com.ssu.ongi.domain.auth.dto.response.ReissueResponse;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.auth.dto.request.SendVerificationRequest;
import com.ssu.ongi.domain.auth.dto.request.VerifyCodeRequest;
import com.ssu.ongi.domain.auth.service.AuthCommandService;
import com.ssu.ongi.domain.auth.service.PhoneVerificationService;
import com.ssu.ongi.domain.auth.service.AuthQueryService;
import com.ssu.ongi.domain.member.dto.request.FindIdRequest;
import com.ssu.ongi.domain.member.dto.request.LoginRequest;
import com.ssu.ongi.domain.member.dto.request.ReissueRequest;
import com.ssu.ongi.domain.member.dto.request.SignupRequest;
import com.ssu.ongi.domain.member.dto.request.UpdatePasswordRequest;
import com.ssu.ongi.domain.auth.dto.response.CheckIdResponse;
import com.ssu.ongi.domain.auth.dto.response.FindIdResponse;
import com.ssu.ongi.domain.auth.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;
    private final PhoneVerificationService phoneVerificationService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        authCommandService.signup(request);
        return ApiResponse.success(SuccessStatus.SIGNUP_SUCCESS);
    }

    @Override
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<CheckIdResponse>> checkLoginId(
            @RequestParam String loginId
    ) {
        CheckIdResponse response = authQueryService.checkLoginId(loginId);
        return ApiResponse.success(SuccessStatus.OK, response);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authCommandService.login(request);
        return ApiResponse.success(SuccessStatus.LOGIN_SUCCESS, response);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal Long memberId
    ) {
        authCommandService.logout(memberId);
        return ApiResponse.success(SuccessStatus.LOGOUT_SUCCESS);
    }

    @Override
    @PostMapping("/find-id")
    public ResponseEntity<ApiResponse<FindIdResponse>> findId(
            @Valid @RequestBody FindIdRequest request
    ) {
        FindIdResponse response = authQueryService.findId(request);
        return ApiResponse.success(SuccessStatus.OK, response);
    }

    @Override
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        authCommandService.updatePassword(request);
        return ApiResponse.success(SuccessStatus.PASSWORD_UPDATE_SUCCESS);
    }

    @Override
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueResponse>> reissue(
            @Valid @RequestBody ReissueRequest request
    ) {
        ReissueResponse response = authCommandService.reissue(request);
        return ApiResponse.success(SuccessStatus.OK, response);
    }

    @PostMapping("/phone/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(
            @Valid @RequestBody SendVerificationRequest request
    ) {
        phoneVerificationService.sendVerificationCode(request);
        return ApiResponse.success(SuccessStatus.OK);
    }

    @PostMapping("/phone/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCode(
            @Valid @RequestBody VerifyCodeRequest request
    ) {
        phoneVerificationService.verifyCode(request);
        return ApiResponse.success(SuccessStatus.OK);
    }
}
