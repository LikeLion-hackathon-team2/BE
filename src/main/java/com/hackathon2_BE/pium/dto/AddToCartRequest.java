package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddToCartRequest(
        @JsonProperty("product_id") Long productId,
        Integer quantity
) {
}
