package com.ssu.ongi.domain.health.controller.docs;

import com.ssu.ongi.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Health", description = "health API")
public interface HealthControllerDocs {

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
                                    """))
            )
    })
    ResponseEntity<ApiResponse<Void>> health();
}
