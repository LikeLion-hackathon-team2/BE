package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "CreateOrderRequest", description = "주문 생성 요청")
public record CreateOrderRequest(
        @Schema(description = "장바구니 항목 ID 목록", example = "[1001, 1002]")
        @JsonProperty("cart_item_ids") List<Long> cartItemIds,

        Shipping shipping,

        @Schema(description = "희망 배송 날짜(로컬 날짜)", example = "2025-09-10")
        @JsonProperty("desired_delivery_date") LocalDate desiredDeliveryDate,

        @Schema(description = "결제 수단", example = "easypay_toss")
        @JsonProperty("payment_method") String paymentMethod
) {
    @Schema(name = "CreateOrderRequest.Shipping", description = "수령인/배송지 정보")
    public record Shipping(
            @Schema(description = "수령인", example = "홍길동")
            @JsonProperty("receiver_name") String receiverName,

            @Schema(description = "연락처", example = "010-1234-5678")
            @JsonProperty("receiver_phone") String receiverPhone,

            @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
            String address
    ){}
}
