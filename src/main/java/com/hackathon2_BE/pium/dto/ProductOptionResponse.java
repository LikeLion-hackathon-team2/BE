package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ProductOptionResponse", description = "상품 옵션/재고/수량 설정")
public record ProductOptionResponse(
        @Schema(description = "상품 ID", example = "101")
        @JsonProperty("product_id") Long productId,

        @Schema(description = "단위 라벨", example = "박스(5kg)")
        @JsonProperty("unit_label") String unitLabel,

        @Schema(description = "단위 가격(원)", example = "28000")
        @JsonProperty("unit_price") Integer unitPrice,

        @Schema(description = "남은 재고 수량", example = "42")
        @JsonProperty("stock_remaining") Integer stockRemaining,

        @Schema(description = "프리셋 수량 옵션", example = "[1,2,3,5]")
        List<Integer> presets,

        Quantity quantity
) {
    @Schema(name = "ProductOptionResponse.Quantity", description = "수량 제한")
    public record Quantity(
            @Schema(description = "최소 수량", example = "1") Integer min,
            @Schema(description = "최대 수량", example = "10") Integer max,
            @Schema(description = "증가 단위", example = "1") Integer step
    ){}
}
