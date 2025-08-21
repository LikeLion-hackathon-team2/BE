package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "OrderPreviewDelivery", description = "주문 미리보기 배송 정보")
public record OrderPreviewDelivery(
        @Schema(description = "요청 희망 날짜", example = "2025-09-05")
        @JsonProperty("requested_date") LocalDate requestedDate,

        @Schema(description = "가장 빠른 가능 날짜", example = "2025-09-06")
        @JsonProperty("earliest_available") LocalDate earliestAvailable,

        @Schema(description = "배송 불가 날짜 목록", example = "[\"2025-09-07\",\"2025-09-08\"]")
        @JsonProperty("unavailable_dates") List<LocalDate> unavailableDates
) {}
