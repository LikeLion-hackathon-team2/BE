package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreateOrderResponse", description = "주문 생성 응답")
public record CreateOrderResponse(
        OrderPart order,
        PaymentPart payment
) {
    @Schema(name = "CreateOrderResponse.OrderPart", description = "주문 본문 정보")
    public record OrderPart(
            @Schema(description = "주문 ID", example = "70001")
            @JsonProperty("order_id") Long orderId,

            @Schema(description = "주문 상태", example = "PENDING_PAYMENT")
            String status,

            @Schema(description = "합계 정보", implementation = OrderPreviewTotals.class)
            OrderPreviewTotals totals
    ){}

    @Schema(name = "CreateOrderResponse.PaymentPart", description = "결제 초기화 결과")
    public record PaymentPart(
            @Schema(description = "결제 프로바이더", example = "toss")
            String provider,

            @Schema(description = "결제 수단", example = "easypay_toss")
            String method,

            @Schema(description = "결제 토큰", example = "tok_test_3c0a9b1f")
            @JsonProperty("payment_token") String paymentToken,

            @Schema(description = "결제 페이지 리다이렉트 URL", example = "https://pay.example.com/redirect/abc123")
            @JsonProperty("redirect_url") String redirectUrl
    ){}
}
