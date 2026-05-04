package com.ssu.ongi.domain.medicine.controller.docs;

import com.ssu.ongi.common.jwt.MemberPrincipal;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.domain.medicine.dto.request.RegisterMedicineScheduleRequest;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Medicine", description = "복약 스케줄 API")
public interface MedicineControllerDocs {

    @Operation(summary = "복약 스케줄 저장", description = "어르신의 복약 스케줄을 등록합니다. 기존 스케줄은 전체 삭제 후 재등록됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "스케줄 저장 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "SCHEDULE_201",
                                      "message": "복약 스케줄이 저장되었습니다."
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "어르신 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_404",
                                      "message": "어르신 정보를 찾을 수 없습니다."
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<Void>> registerSchedules(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody RegisterMedicineScheduleRequest request
    );

    @Operation(summary = "복약 스케줄 목록 조회", description = "어르신의 전체 복약 스케줄을 시간순으로 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<List<MedicineScheduleResponse>>> getSchedules(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "어르신 ID", required = true) @RequestParam Long elderId
    );

    @Operation(summary = "복약 스케줄 삭제", description = "복약 스케줄을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스케줄을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "약 ID", required = true) @PathVariable Long medicineId
    );
}
