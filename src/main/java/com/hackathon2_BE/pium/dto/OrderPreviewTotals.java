package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "OrderPreviewTotals", description = "주문 금액 합계")
public record OrderPreviewTotals(
        @Schema(description = "상품 합계(원)", example = "32000") Integer products,
        @Schema(description = "배송비(원)", example = "0") Integer shipping,
        @Schema(description = "총 결제금액(원)", example = "32000")
        @JsonProperty("grand_total") Integer grandTotal
) {}
