package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CartItemView", description = "장바구니 항목 뷰")
public record CartItemView(
        @Schema(description = "장바구니 항목 ID", example = "1001")
        @JsonProperty("cart_item_id") Long cartItemId,

        @Schema(description = "상품 ID", example = "101")
        @JsonProperty("product_id") Long productId,

        @Schema(description = "상품명", example = "신선한 사과 5kg")
        String name,

        @Schema(description = "수량", example = "2")
        Integer quantity,

        @Schema(description = "단가(원)", example = "15000")
        @JsonProperty("unit_price") Integer unitPrice,

        @Schema(description = "대표 이미지 URL", example = "https://cdn.example.com/img/apple.jpg")
        @JsonProperty("image_url") String imageUrl,

        Seller seller,

        @Schema(description = "상품 스펙/설명", example = "당도 13Brix, 산지 직송")
        String spec
) {
    @Schema(name = "CartItemView.Seller", description = "판매자 요약")
    public record Seller(
            @Schema(description = "판매자 사용자 ID", example = "501")
            @JsonProperty("user_id") Long userId,

            @Schema(description = "상호명", example = "청송농원")
            @JsonProperty("shop_name") String shopName
    ) {}
}
