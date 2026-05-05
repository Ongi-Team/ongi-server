package com.ssu.ongi.domain.member.controller.docs;

import com.ssu.ongi.common.jwt.MemberPrincipal;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.domain.member.dto.request.FcmTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "FCM 토큰 등록/갱신", description = "로그인, 토큰 만료, 앱 재설치 시 FCM 토큰을 등록 또는 갱신합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "FCM 토큰 등록/갱신 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "MEMBER_200",
                                      "message": "FCM 토큰이 등록되었습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "FCM 토큰 또는 OS 타입 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON_400",
                                      "message": "FCM 토큰은 필수입니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON_401",
                                      "message": "인증이 필요합니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> registerFcmToken(
            MemberPrincipal principal,
            @Valid @RequestBody FcmTokenRequest request
    );

    @Operation(summary = "FCM 토큰 삭제", description = "로그아웃 또는 회원탈퇴 시 FCM 토큰을 삭제합니다. 토큰이 없을 경우 무시됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "FCM 토큰 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "MEMBER_200_2",
                                      "message": "FCM 토큰이 삭제되었습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON_401",
                                      "message": "인증이 필요합니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> deleteFcmToken(MemberPrincipal principal);
}
