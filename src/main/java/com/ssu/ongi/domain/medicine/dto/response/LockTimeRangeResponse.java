package com.ssu.ongi.domain.medicine.dto.response;

import java.time.LocalTime;

public record LockTimeRangeResponse(
        LocalTime lockStartAt,
        LocalTime lockEndAt
) {
}
