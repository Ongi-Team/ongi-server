package com.ssu.ongi.domain.medicine.controller;

import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.domain.medicine.dto.request.RegisterMedicineScheduleRequest;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleResponse;
import com.ssu.ongi.domain.medicine.dto.response.MedicineScheduleSaveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Medicine Schedule", description = "복약 스케줄 API")
public interface MedicineScheduleControllerDocs {

    @Operation(summary = "복약 스케줄 저장", description = "어르신의 복약 스케줄을 등록합니다. 시간순으로 약통 번호가 자동 할당됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "스케줄 저장 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MedicineScheduleSaveResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "SCHEDULE_201",
                                      "message": "복약 스케줄이 저장되었습니다.",
                                      "data": {
                                        "schedules": [
                                          { "scheduleId": 1, "medicineId": 1, "name": "혈압약", "dispenserSlot": 1, "scheduledTime": "08:00" },
                                          { "scheduleId": 2, "medicineId": 2, "name": "당뇨약", "dispenserSlot": 2, "scheduledTime": "12:00" }
                                        ],
                                        "lockTimeRange": { "lockStart": "08:00", "lockEnd": "12:00" }
                                      }
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "어르신 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH_404_2",
                                      "message": "어르신 정보를 찾을 수 없습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "동일 시간 스케줄 중복",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SCHEDULE_409",
                                      "message": "동일한 시간에 이미 스케줄이 존재합니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<MedicineScheduleSaveResponse>> saveSchedules(
            @Valid @RequestBody RegisterMedicineScheduleRequest request
    );

    @Operation(summary = "복약 스케줄 목록 조회", description = "어르신의 전체 복약 스케줄을 시간순으로 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "스케줄 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "SCHEDULE_200",
                                      "message": "복약 스케줄 조회에 성공하였습니다.",
                                      "data": [
                                        { "scheduleId": 1, "medicineId": 1, "name": "혈압약", "dispenserSlot": 1, "scheduledTime": "08:00" },
                                        { "scheduleId": 2, "medicineId": 2, "name": "당뇨약", "dispenserSlot": 2, "scheduledTime": "12:00" }
                                      ]
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<List<MedicineScheduleResponse>>> getSchedules(
            @Parameter(description = "어르신 ID", required = true, example = "1")
            @RequestParam Long elderId
    );

    @Operation(summary = "복약 스케줄 삭제", description = "복약 스케줄을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "스케줄 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "SCHEDULE_200_2",
                                      "message": "복약 스케줄이 삭제되었습니다."
                                    }
                                    """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "스케줄을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SCHEDULE_404",
                                      "message": "복약 스케줄을 찾을 수 없습니다."
                                    }
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @Parameter(description = "스케줄 ID", required = true, example = "1")
            @PathVariable Long scheduleId,
            @Parameter(description = "어르신 ID", required = true, example = "1")
            @RequestParam Long elderId
    );
}
