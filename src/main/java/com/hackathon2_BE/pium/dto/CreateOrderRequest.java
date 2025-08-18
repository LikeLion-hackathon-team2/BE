package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record CreateOrderRequest(
        @JsonProperty("cart_item_ids") List<Long> cartItemIds,
        Shipping shipping,
        @JsonProperty("desired_delivery_date") LocalDate desiredDeliveryDate,
        @JsonProperty("payment_method") String paymentMethod
) {
    public record Shipping(
            @JsonProperty("receiver_name") String receiverName,
            @JsonProperty("receiver_phone") String receiverPhone,
            String address
    ){}
}
