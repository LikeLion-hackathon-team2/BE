package com.hackathon2_BE.pium.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "MonthlyEventsResponse", description = "월간 이벤트 응답")
public class MonthlyEventsResponse {

    @Schema(description = "요청 월(yyyy-MM)", example = "2025-09")
    private String month;

    @Schema(description = "타임존", example = "Asia/Seoul")
    private String timezone;

    @Schema(description = "날짜별 이벤트 목록")
    private List<MonthlyEventDayDto> days;
}
