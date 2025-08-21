package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "OrderPreviewRequest", description = "주문 미리보기 요청")
public record OrderPreviewRequest(
        @Schema(description = "장바구니 항목 ID 목록", example = "[11,12]")
        @JsonProperty("cart_item_ids") List<Long> cartItemIds,

        @Schema(description = "희망 배송일", example = "2025-09-06")
        @JsonProperty("desired_delivery_date") LocalDate desiredDeliveryDate
) {}
