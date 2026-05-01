package com.ssu.ongi.domain.device.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.device.dto.request.HeartbeatRequest;
import com.ssu.ongi.domain.device.service.DeviceConnectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeviceConnectionController implements DeviceConnectionControllerDocs{
	private final DeviceConnectionService deviceConnectionService;

	@Override
	@PostMapping("device/{serialNumber}/heartbeat")
	public ResponseEntity<ApiResponse<Void>> heartbeat(
		@PathVariable String serialNumber,
		@Valid @RequestBody HeartbeatRequest request
	) {
		deviceConnectionService.updateHeartbeat(serialNumber, request);
		return ApiResponse.success(SuccessStatus.DEVICE_CONNECTION_SUCCESS);
	}

	@Override
	@GetMapping("/health")
	public ResponseEntity<ApiResponse<Void>> health() {
		return ApiResponse.success(SuccessStatus.OK);
	}
}
