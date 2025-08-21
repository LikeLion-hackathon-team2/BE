package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CartItemResponse", description = "장바구니 항목 응답")
public record CartItemResponse(
        @Schema(description = "장바구니 항목 ID", example = "1001")
        @JsonProperty("cart_item_id") Long cartItemId,

        @Schema(description = "상품 ID", example = "101")
        @JsonProperty("product_id") Long productId,

        @Schema(description = "수량", example = "2")
        Integer quantity,

        @Schema(description = "단가(원)", example = "15000")
        @JsonProperty("unit_price") Integer unitPrice,

        @Schema(description = "소계(원) = 수량*단가", example = "30000")
        Integer subtotal
) {}
