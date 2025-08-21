package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AddToCartRequest", description = "장바구니 담기 요청")
public record AddToCartRequest(
        @Schema(description = "상품 ID", example = "101")
        @JsonProperty("product_id") Long productId,

        @Schema(description = "수량", example = "3")
        Integer quantity
) {}
