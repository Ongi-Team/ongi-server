package com.ssu.ongi.domain.device.controller;

import com.ssu.ongi.common.jwt.MemberPrincipal;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.device.dto.request.DeviceRegisterRequest;
import com.ssu.ongi.domain.device.service.DeviceCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Tag(name = "Device", description = "디바이스 API")
public class DeviceController implements DeviceControllerDocs {

    private final DeviceCommandService deviceCommandService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registerDevice(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody DeviceRegisterRequest request
    ) {
        deviceCommandService.registerDevice(principal.memberId(), principal.loginMode(), request);
        return ApiResponse.success(SuccessStatus.DEVICE_REGISTER_SUCCESS);
    }
}
