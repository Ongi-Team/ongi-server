package com.ssu.ongi.domain.auth.controller;

import com.ssu.ongi.domain.member.dto.response.ReissueResponse;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.domain.auth.dto.request.SendVerificationRequest;
import com.ssu.ongi.domain.auth.dto.request.VerifyCodeRequest;
import com.ssu.ongi.domain.member.dto.request.FindIdRequest;
import com.ssu.ongi.domain.member.dto.request.LoginRequest;
import com.ssu.ongi.domain.member.dto.request.ReissueRequest;
import com.ssu.ongi.domain.member.dto.request.SignupRequest;
import com.ssu.ongi.domain.member.dto.request.UpdatePasswordRequest;
import com.ssu.ongi.domain.member.dto.response.CheckIdResponse;
import com.ssu.ongi.domain.member.dto.response.FindIdResponse;
import com.ssu.ongi.domain.member.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Auth", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "회원가입", description = "보호자 정보와 어르신 정보를 함께 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "AUTH_201",
                                      "message": "회원가입이 완료되었습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유효성 검사 실패 (아이디/비밀번호/이름/전화번호/어르신 정보 누락 또는 비밀번호 형식 오류)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON_400",
                                      "message": "비밀번호는 8자 이상, 영문/숫자/특수문자 중 2가지 이상 포함해야 합니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "아이디 중복",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_409",
                                      "message": "이미 사용 중인 아이디입니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request);


    @Operation(summary = "아이디 중복 확인", description = "아이디 사용 가능 여부를 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "중복 확인 성공 (available: true면 사용 가능, false면 중복)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckIdResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON_200",
                                      "message": "요청에 성공하였습니다.",
                                      "data": {
                                        "available": true
                                      }
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "loginId 파라미터 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON_400",
                                      "message": "잘못된 요청입니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<CheckIdResponse>> checkLoginId(
            @Parameter(description = "중복 확인할 아이디", required = true, example = "hong1234")
            @RequestParam String loginId
    );


    @Operation(summary = "로그인", description = """
            아이디/비밀번호로 로그인하고 모드를 선택합니다.
            - GUARDIAN 모드: 보호자 정보 + 등록된 모든 어르신 목록 반환
            - ELDER 모드: 첫 번째 어르신 정보만 반환
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = {
                                    @ExampleObject(name = "GUARDIAN 모드", value = """
                                            {
                                              "isSuccess": true,
                                              "code": "AUTH_200",
                                              "message": "로그인에 성공하였습니다.",
                                              "data": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                "loginMode": "GUARDIAN",
                                                "member": { "memberId": 1, "name": "홍길동", "phone": "010-1234-5678" },
                                                "elders": [{ "elderId": 1, "name": "홍부모", "age": 75, "relationship": "부모" }]
                                              }
                                            }
                                            """),
                                    @ExampleObject(name = "ELDER 모드", value = """
                                            {
                                              "isSuccess": true,
                                              "code": "AUTH_200",
                                              "message": "로그인에 성공하였습니다.",
                                              "data": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                "loginMode": "ELDER",
                                                "elder": { "elderId": 1, "name": "홍부모", "age": 75, "relationship": "부모" }
                                              }
                                            }
                                            """)
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유효성 검사 실패 (아이디/비밀번호/로그인 모드 누락)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON_400",
                                      "message": "로그인 모드를 선택해주세요."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "아이디 또는 비밀번호 불일치",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_401",
                                      "message": "아이디 또는 비밀번호가 올바르지 않습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "어르신 정보 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_404_2",
                                      "message": "어르신 정보를 찾을 수 없습니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request);


    @Operation(summary = "아이디 찾기", description = "전화번호로 아이디를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "아이디 찾기 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FindIdResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON_200",
                                      "message": "요청에 성공하였습니다.",
                                      "data": { "loginId": "hong1234" }
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "전화번호 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON_400",
                                      "message": "전화번호를 입력해주세요."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "해당 전화번호로 가입된 회원 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_404",
                                      "message": "회원을 찾을 수 없습니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<FindIdResponse>> findId(@Valid @RequestBody FindIdRequest request);


    @Operation(summary = "비밀번호 변경", description = "아이디와 전화번호 인증 후 비밀번호를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "비밀번호 변경 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "AUTH_200_1",
                                      "message": "비밀번호가 변경되었습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유효성 검사 실패 (아이디/전화번호/새 비밀번호 누락 또는 형식 오류)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON_400",
                                      "message": "비밀번호는 8자 이상, 영문/숫자/특수문자 중 2가지 이상 포함해야 합니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "아이디와 전화번호가 일치하는 회원 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_404",
                                      "message": "회원을 찾을 수 없습니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> updatePassword(@Valid @RequestBody UpdatePasswordRequest request);

    @Operation(summary = "토큰 재발급", description = "RefreshToken으로 AccessToken과 RefreshToken을 재발급합니다. (RTR 전략 - 재발급 시 기존 RefreshToken 폐기)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON_200",
                                      "message": "요청에 성공하였습니다.",
                                      "data": {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                                      }
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "만료된 토큰 또는 이미 사용된 토큰 (재사용 감지 시 강제 로그아웃)",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "만료된 토큰", value = """
                                            {
                                              "isSuccess": false,
                                              "code": "JWT_401_1",
                                              "message": "만료된 토큰입니다."
                                            }
                                            """),
                                    @ExampleObject(name = "토큰 재사용 감지", value = """
                                            {
                                              "isSuccess": false,
                                              "code": "JWT_401_4",
                                              "message": "이미 사용된 토큰입니다. 다시 로그인해주세요."
                                            }
                                            """)
                            })
            )
    })
    ResponseEntity<ApiResponse<ReissueResponse>> reissue(@Valid @RequestBody ReissueRequest request);

    @Operation(summary = "전화번호 인증 코드 발송", description = "입력한 전화번호로 6자리 인증번호를 SMS 발송합니다. 인증번호는 3분간 유효합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "인증번호 발송 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON_200",
                                      "message": "요청에 성공하였습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "전화번호 누락 또는 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON_400",
                                      "message": "올바른 전화번호 형식이 아닙니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> sendVerificationCode(@Valid @RequestBody SendVerificationRequest request);

    @Operation(summary = "전화번호 인증 코드 확인", description = "발송된 6자리 인증번호를 검증합니다. 인증 성공 시 10분간 인증 완료 상태가 유지됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "인증 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON_200",
                                      "message": "요청에 성공하였습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "인증번호 불일치",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "PHONE_400",
                                      "message": "인증번호가 올바르지 않습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "인증번호 만료 또는 미발송 (3분 초과)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "PHONE_404",
                                      "message": "인증번호가 만료되었거나 존재하지 않습니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> verifyCode(@Valid @RequestBody VerifyCodeRequest request);
}
