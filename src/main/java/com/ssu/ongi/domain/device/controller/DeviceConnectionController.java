package com.ssu.ongi.domain.device.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeviceConnectionController implements DeviceConnectionControllerDocs{

	@Override
	@GetMapping("/health")
	public ResponseEntity<ApiResponse<Void>> health() {
		return ApiResponse.success(SuccessStatus.OK);
	}
}
