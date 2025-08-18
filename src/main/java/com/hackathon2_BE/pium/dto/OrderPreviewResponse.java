package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrderPreviewResponse(
        List<OrderPreviewItem> items,
        OrderPreviewTotals totals,
        OrderPreviewDelivery delivery,
        @JsonProperty("payment_methods") List<SimpleMethod> paymentMethods
) {
    public record SimpleMethod(String key, String label){}
}
