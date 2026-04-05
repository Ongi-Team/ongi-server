package com.ssu.ongi.domain.medicine.controller;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.medicine.dto.request.MedicationRecordSyncRequest;
import com.ssu.ongi.domain.medicine.dto.response.MedicationRecordResponse;
import com.ssu.ongi.domain.medicine.service.MedicationRecordCommandService;
import com.ssu.ongi.domain.medicine.service.MedicationRecordQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MedicationRecordController implements MedicationRecordControllerDocs {

    private final MedicationRecordCommandService medicationRecordCommandService;
    private final MedicationRecordQueryService medicationRecordQueryService;

    @Override
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<Void>> syncRecords(
            @Valid @RequestBody MedicationRecordSyncRequest request
    ) {
        medicationRecordCommandService.syncOfflineRecords(request);
        return ApiResponse.success(SuccessStatus.SYNC_MEDICATION_RECORD_SUCCESS);
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicationRecordResponse>>> getRecordsByDate(
            @RequestParam Long elderId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<MedicationRecordResponse> response = medicationRecordQueryService.getRecordsByDate(elderId, date);
        return ApiResponse.success(SuccessStatus.GET_MEDICATION_RECORD_SUCCESS, response);
    }

    @Override
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<ApiResponse<List<MedicationRecordResponse>>> getRecordsBySchedule(
            @PathVariable Long scheduleId,
            @RequestParam Long elderId
    ) {
        List<MedicationRecordResponse> response = medicationRecordQueryService.getRecordsBySchedule(scheduleId, elderId);
        return ApiResponse.success(SuccessStatus.GET_MEDICATION_RECORD_SUCCESS, response);
    }
}
