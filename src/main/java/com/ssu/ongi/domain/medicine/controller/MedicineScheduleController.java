package com.ssu.ongi.domain.medicine.controller;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.medicine.dto.request.MedicineScheduleCreateRequest;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleSaveResponse;
import com.ssu.ongi.domain.medicine.service.MedicineScheduleCommandService;
import com.ssu.ongi.domain.medicine.service.MedicineScheduleQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class MedicineScheduleController implements MedicineScheduleControllerDocs {

    private final MedicineScheduleCommandService medicineScheduleCommandService;
    private final MedicineScheduleQueryService medicineScheduleQueryService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<MedicineScheduleSaveResponse>> saveSchedules(
            @Valid @RequestBody MedicineScheduleCreateRequest request
    ) {
        MedicineScheduleSaveResponse response = medicineScheduleCommandService.saveSchedules(request);
        return ApiResponse.success(SuccessStatus.REGISTER_MEDICINE_SCHEDULE_SUCCESS, response);
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicineScheduleResponse>>> getSchedules(
            @RequestParam Long elderId
    ) {
        List<MedicineScheduleResponse> response = medicineScheduleQueryService.getSchedules(elderId);
        return ApiResponse.success(SuccessStatus.GET_MEDICINE_SCHEDULE_SUCCESS, response);
    }

    @Override
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable Long scheduleId,
            @RequestParam Long elderId
    ) {
        medicineScheduleCommandService.deleteSchedule(scheduleId, elderId);
        return ApiResponse.success(SuccessStatus.DELETE_MEDICINE_SCHEDULE_SUCCESS);
    }
}
