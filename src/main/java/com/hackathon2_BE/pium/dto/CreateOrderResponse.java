package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateOrderResponse(
        OrderPart order,
        PaymentPart payment
) {
    public record OrderPart(
            @JsonProperty("order_id") Long orderId,
            String status,
            OrderPreviewTotals totals
    ){}
    public record PaymentPart(
            String provider, String method,
            @JsonProperty("payment_token") String paymentToken,
            @JsonProperty("redirect_url") String redirectUrl
    ){}
}
