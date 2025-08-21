package com.hackathon2_BE.pium.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "MonthlyEventDayDto", description = "월간 이벤트: 하루 단위")
public class MonthlyEventDayDto {

    @Schema(description = "날짜", example = "2025-09-10")
    private LocalDate date;

    @Schema(description = "해당 날짜의 이벤트 목록")
    private List<MonthlyEventItemDto> items;
}
