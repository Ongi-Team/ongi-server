package com.ssu.ongi.domain.medicine.controller;

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

    @Operation(summary = "오프라인 복약 기록 동기화", description = "디바이스에서 수집된 복약 기록을 일괄 동기화합니다. 중복 기록은 자동으로 무시됩니다.")
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "디바이스 또는 스케줄을 찾을 수 없음",
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
    ResponseEntity<ApiResponse<Void>> syncRecords(
            @Valid @RequestBody MedicationRecordSyncRequest request
    );

    @Operation(summary = "날짜별 복약 기록 조회", description = "특정 날짜의 어르신 복약 기록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "RECORD_200_2",
                                      "message": "복약 기록 조회에 성공하였습니다.",
                                      "data": [
                                        { "recordId": 1, "medicineName": "혈압약", "scheduledTime": "08:00", "result": "TAKEN", "recordedAt": "2026-04-05T08:05:00" },
                                        { "recordId": 2, "medicineName": "당뇨약", "scheduledTime": "12:00", "result": "MISSED", "recordedAt": "2026-04-05T12:30:00" }
                                      ]
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<List<MedicationIntakeResponse>>> getRecordsByDate(
            @Parameter(description = "어르신 ID", required = true, example = "1")
            @RequestParam Long elderId,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", required = true, example = "2026-04-05")
            @RequestParam LocalDate date
    );

    @Operation(summary = "스케줄별 복약 이력 조회", description = "특정 복약 스케줄의 전체 복약 이력을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "RECORD_200_2",
                                      "message": "복약 기록 조회에 성공하였습니다.",
                                      "data": [
                                        { "recordId": 1, "medicineName": "혈압약", "scheduledTime": "08:00", "result": "TAKEN", "recordedAt": "2026-04-04T08:03:00" },
                                        { "recordId": 3, "medicineName": "혈압약", "scheduledTime": "08:00", "result": "TAKEN", "recordedAt": "2026-04-05T08:01:00" }
                                      ]
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<List<MedicationIntakeResponse>>> getRecordsBySchedule(
            @Parameter(description = "스케줄 ID", required = true, example = "1")
            @PathVariable Long scheduleId,
            @Parameter(description = "어르신 ID", required = true, example = "1")
            @RequestParam Long elderId
    );
}
