package com.ssu.ongi.domain.device.controller.docs;

import com.ssu.ongi.common.jwt.MemberPrincipal;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.domain.device.dto.request.DeviceRegisterRequest;
import com.ssu.ongi.domain.device.dto.request.HeartbeatRequest;
import com.ssu.ongi.domain.device.dto.request.MedicationStatusRequest;
import com.ssu.ongi.domain.device.dto.response.RegisterDeviceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Device", description = "기기 API")
public interface DeviceControllerDocs {

    @Operation(summary = "디바이스 등록", description = "보호자가 어르신의 디바이스를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "디바이스 등록 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": true,
                                        "code": "DEVICE_201",
                                        "message": "디바이스가 등록되었습니다.",
                                        "data": { "deviceToken": "550e8400-e29b-41d4-a716-446655440000" }
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "어르신 모드 접근 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 디바이스")
    })
    ResponseEntity<ApiResponse<RegisterDeviceResponse>> registerDevice(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody DeviceRegisterRequest request
    );

    @Operation(summary = "디바이스 heartbeat", description = "디바이스의 연결 상태를 업데이트합니다.")
    @Parameter(name = "Device-Token", in = ParameterIn.HEADER, required = true, description = "디바이스 인증 토큰")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "heartbeat 수신 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Device-Token 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "등록되지 않은 디바이스 토큰")
    })
    ResponseEntity<ApiResponse<Void>> heartbeat(
            @Parameter(hidden = true) @RequestAttribute Long deviceId,
            @Valid @RequestBody HeartbeatRequest request
    );

    @Operation(summary = "전체 약통 열기", description = "보호자가 전체 약통 열기 명령을 디바이스에 전달합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "명령 전달 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "어르신 모드 접근 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "등록된 디바이스 없음")
    })
    ResponseEntity<ApiResponse<Void>> openAll(
            @AuthenticationPrincipal MemberPrincipal principal
    );

    @Operation(summary = "전체 약통 닫기", description = "보호자가 전체 약통 닫기 명령을 디바이스에 전달합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "명령 전달 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "어르신 모드 접근 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "등록된 디바이스 없음")
    })
    ResponseEntity<ApiResponse<Void>> closeAll(
            @AuthenticationPrincipal MemberPrincipal principal
    );

    @Operation(summary = "복약 상태 업데이트", description = "디바이스가 복약 완료/미복약 상태를 전송합니다.")
    @Parameter(name = "Device-Token", in = ParameterIn.HEADER, required = true, description = "디바이스 인증 토큰")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "복약 상태 업데이트 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Device-Token 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "슬롯을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<Void>> updateMedicationStatus(
            @Parameter(hidden = true) @RequestAttribute Long deviceId,
            @Valid @RequestBody MedicationStatusRequest request
    );
}
