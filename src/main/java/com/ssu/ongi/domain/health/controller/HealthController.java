package com.ssu.ongi.domain.health.controller;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController implements HealthControllerDocs {

    @Override
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Void>> health() {
        return ApiResponse.success(SuccessStatus.OK);
    }
}
