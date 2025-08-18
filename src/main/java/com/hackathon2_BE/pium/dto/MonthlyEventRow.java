package com.hackathon2_BE.pium.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyEventRow {
    private Long eventId;
    private String title;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String categoryName;
    private String iconEmoji;
    private String iconImageUrl;
}
