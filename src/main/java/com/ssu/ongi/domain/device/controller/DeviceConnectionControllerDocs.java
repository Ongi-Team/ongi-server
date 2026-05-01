package com.ssu.ongi.domain.device.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.domain.device.dto.request.HeartbeatRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;


public interface DeviceConnectionControllerDocs {
	@Operation(summary = "디바이스 heartbeat", description = "디바이스의 연결 상태를 확인합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "디바이스 ONLINE",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
			{
				"isSuccess": true,
				"code": "DEVICE_200",
				"message": "디바이스가 연결되었습니다."
			}
			"""))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "디바이스 정보 없음",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
						"isSuccess": false,
						"code": "DEVICE_404",
						"message": "디바이스를 찾을 수 없습니다."
					}
					"""))
		)
	})
	ResponseEntity<ApiResponse<Void>> heartbeat(
		@PathVariable String serialNumber,
		@Valid @RequestBody HeartbeatRequest request
	);

	@Operation(summary = "서버 health check", description = "서버 정상 작동 여부를 확인합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "서버 정상",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
						"isSuccess": true,
						"code": "COMMON_200",
						"message": "성공입니다."
					}
					""")
			)
		)
	})
	ResponseEntity<ApiResponse<Void>> health();
}
