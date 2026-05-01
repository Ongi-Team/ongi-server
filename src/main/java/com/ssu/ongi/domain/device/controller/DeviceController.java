package com.ssu.ongi.domain.device.controller;

import com.ssu.ongi.common.jwt.MemberPrincipal;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.device.dto.request.DeviceRegisterRequest;
import com.ssu.ongi.domain.device.dto.request.HeartbeatRequest;
import com.ssu.ongi.domain.device.dto.response.RegisterDeviceResponse;
import com.ssu.ongi.domain.device.service.DeviceCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController implements DeviceControllerDocs {

    private final DeviceCommandService deviceCommandService;

    @Override
    @PostMapping("")
    public ResponseEntity<ApiResponse<RegisterDeviceResponse>> registerDevice(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody DeviceRegisterRequest request
    ) {
        RegisterDeviceResponse response = deviceCommandService.registerDevice(
                principal.memberId(), principal.loginMode(), request
        );
        return ApiResponse.success(SuccessStatus.DEVICE_REGISTER_SUCCESS, response);
    }

    @Override
    @PostMapping("/heartbeat")
    public ResponseEntity<ApiResponse<Void>> heartbeat(
            @RequestAttribute Long deviceId,
            @Valid @RequestBody HeartbeatRequest request
    ) {
        deviceCommandService.updateHeartbeat(deviceId, request);
        return ApiResponse.success(SuccessStatus.DEVICE_CONNECTION_SUCCESS);
    }
}
