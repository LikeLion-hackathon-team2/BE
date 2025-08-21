package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "OrderPreviewItem", description = "주문 미리보기 항목")
public record OrderPreviewItem(
        @Schema(description = "장바구니 항목 ID", example = "11")
        @JsonProperty("cart_item_id") Long cartItemId,

        @Schema(description = "상품 ID", example = "101")
        @JsonProperty("product_id") Long productId,

        @Schema(description = "상품명", example = "청송 사과 5kg")
        String name,

        @Schema(description = "수량", example = "2")
        Integer quantity,

        @Schema(description = "단가(원)", example = "16000")
        @JsonProperty("unit_price") Integer unitPrice,

        @Schema(description = "소계(원)", example = "32000")
        Integer subtotal,

        @Schema(description = "상품 이미지 URL", example = "https://cdn.example.com/img/apple.jpg")
        @JsonProperty("image_url") String imageUrl,

        Seller seller,

        @Schema(description = "규격/옵션 요약", example = "상/5kg 박스")
        String spec
) {
    @Schema(name = "OrderPreviewItem.Seller", description = "판매자 정보")
    public record Seller(
            @Schema(description = "판매자 사용자 ID", example = "3")
            @JsonProperty("user_id") Long userId,

            @Schema(description = "상점명", example = "청송농원")
            @JsonProperty("shop_name") String shopName
    ) {}
}
