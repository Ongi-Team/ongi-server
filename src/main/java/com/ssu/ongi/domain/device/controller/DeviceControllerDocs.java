package com.ssu.ongi.domain.device.controller;

import com.ssu.ongi.common.jwt.MemberPrincipal;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.domain.device.dto.request.DeviceRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

public interface DeviceControllerDocs {

    @Operation(summary = "디바이스 등록", description = "보호자가 어르신의 디바이스를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "디바이스 등록 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": true,
                                        "code": "DEVICE_201",
                                        "message": "디바이스가 등록되었습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "어르신 모드 접근 불가",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "DEVICE_403",
                                        "message": "어르신 모드에서는 디바이스 등록이 불가능합니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 등록된 디바이스",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "DEVICE_409",
                                        "message": "이미 등록된 디바이스입니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> registerDevice(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody DeviceRegisterRequest request
    );
}
