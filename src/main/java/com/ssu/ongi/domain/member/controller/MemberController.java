package com.ssu.ongi.domain.member.controller;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.member.dto.request.FcmTokenRequest;
import com.ssu.ongi.domain.member.service.MemberCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {

    private final MemberCommandService memberCommandService;

    @Override
    @PostMapping("/fcm-token")
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody FcmTokenRequest request
    ) {
        memberCommandService.registerFcmToken(memberId, request);
        return ApiResponse.success(SuccessStatus.FCM_TOKEN_REGISTER_SUCCESS);
    }
}
