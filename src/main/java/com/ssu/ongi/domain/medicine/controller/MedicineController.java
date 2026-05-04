package com.ssu.ongi.domain.medicine.controller;

import com.ssu.ongi.common.jwt.MemberPrincipal;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.SuccessStatus;
import com.ssu.ongi.domain.medicine.controller.docs.MedicineControllerDocs;
import com.ssu.ongi.domain.medicine.dto.request.RegisterMedicineScheduleRequest;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import com.ssu.ongi.domain.medicine.service.MedicineCommandService;
import com.ssu.ongi.domain.medicine.service.MedicineQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/medicine/schedules")
@RequiredArgsConstructor
public class MedicineController implements MedicineControllerDocs {

    private final MedicineCommandService medicineCommandService;
    private final MedicineQueryService medicineQueryService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registerSchedules(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody RegisterMedicineScheduleRequest request
    ) {
        medicineCommandService.registerSchedules(principal.memberId(), request);
        return ApiResponse.success(SuccessStatus.REGISTER_MEDICINE_SCHEDULE_SUCCESS);
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicineScheduleResponse>>> getSchedules(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam Long elderId
    ) {
        List<MedicineScheduleResponse> response = medicineQueryService.getSchedules(principal.memberId(), elderId);
        return ApiResponse.success(SuccessStatus.GET_MEDICINE_SCHEDULE_SUCCESS, response);
    }

    @Override
    @DeleteMapping("/{medicineId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long medicineId
    ) {
        medicineCommandService.deleteSchedule(principal.memberId(), medicineId);
        return ApiResponse.success(SuccessStatus.DELETE_MEDICINE_SCHEDULE_SUCCESS);
    }
}
