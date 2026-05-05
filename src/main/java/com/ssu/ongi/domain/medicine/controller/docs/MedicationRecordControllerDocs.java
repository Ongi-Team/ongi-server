package com.ssu.ongi.domain.medicine.controller.docs;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.domain.medicine.dto.request.MedicationRecordSyncRequest;
import com.ssu.ongi.domain.medicine.dto.response.MedicationIntakeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Medicine Medication", description = "복약 기록 API")
public interface MedicationRecordControllerDocs {

    @Operation(summary = "오프라인 복약 기록 동기화", description = "디바이스에서 수집된 복약 기록을 일괄 동기화합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "동기화 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "RECORD_200",
                                      "message": "복약 기록 동기화에 성공하였습니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> syncRecords(
            @Valid @RequestBody MedicationRecordSyncRequest request
    );

    @Operation(summary = "날짜별 복약 기록 조회", description = "특정 날짜의 어르신 복약 기록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<ApiResponse<List<MedicationIntakeResponse>>> getRecordsByDate(
            @Parameter(description = "어르신 ID", required = true) @RequestParam Long elderId,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", required = true) @RequestParam LocalDate date
    );

    @Operation(summary = "약별 복약 이력 조회", description = "특정 약의 전체 복약 이력을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<ApiResponse<List<MedicationIntakeResponse>>> getRecordsByMedicine(
            @Parameter(description = "약 ID", required = true) @PathVariable Long medicineId,
            @Parameter(description = "어르신 ID", required = true) @RequestParam Long elderId
    );
}
