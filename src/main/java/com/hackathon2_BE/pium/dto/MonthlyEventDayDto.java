package com.hackathon2_BE.pium.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyEventDayDto {
    private LocalDate date;
    private List<MonthlyEventItemDto> items;
}
