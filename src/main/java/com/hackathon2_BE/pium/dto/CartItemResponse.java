package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CartItemResponse(
        @JsonProperty("cart_item_id") Long cartItemId,
        @JsonProperty("product_id") Long productId,
        Integer quantity,
        @JsonProperty("unit_price") Integer unitPrice,
        Integer subtotal
) {
}
