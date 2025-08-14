package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CartItemView(
        @JsonProperty("cart_item_id") Long cartItemId,
        @JsonProperty("product_id") Long productId,
        String name,
        Integer quantity,
        @JsonProperty("unit_price") Integer unitPrice,
        @JsonProperty("image_url") String imageUrl,
        Seller seller,
        String spec
) {
    public record Seller(@JsonProperty("user_id") Long userId,
                         @JsonProperty("shop_name") String shopName) {}
}
