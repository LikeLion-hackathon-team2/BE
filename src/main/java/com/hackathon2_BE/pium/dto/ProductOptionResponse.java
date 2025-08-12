package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProductOptionResponse(
        @JsonProperty("product_id") Long productId,
        @JsonProperty("unit_label") String unitLabel,
        @JsonProperty("unit_price") Integer unitPrice,
        @JsonProperty("stock_remaining") Integer stockRemaining,
        List<Integer> presets,
        Quantity quantity
) {
    public record Quantity(Integer min, Integer max, Integer step){}
}
