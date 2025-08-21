package com.hackathon2_BE.pium.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "MonthlyEventRow", description = "월간 이벤트 단순 행 뷰")
public class MonthlyEventRow {

    @Schema(description = "이벤트 ID", example = "9001")
    private Long eventId;

    @Schema(description = "제목", example = "사과 공동구매 오픈")
    private String title;

    @Schema(description = "설명", example = "청송 사과 5kg 한정 특가")
    private String description;

    @Schema(description = "시작 시각", example = "2025-09-10T09:00:00")
    private LocalDateTime startAt;

    @Schema(description = "종료 시각", example = "2025-09-10T18:00:00")
    private LocalDateTime endAt;

    @Schema(description = "카테고리명", example = "공동구매")
    private String categoryName;

    @Schema(description = "아이콘 이모지", example = "🍎")
    private String iconEmoji;

    @Schema(description = "아이콘 이미지 URL", example = "https://cdn.example.com/icons/apple.png")
    private String iconImageUrl;
}
