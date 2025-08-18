package com.hackathon2_BE.pium.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyEventsResponse {
    private String month;
    private String timezone;
    private List<MonthlyEventDayDto> days;
}
