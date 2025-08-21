package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "OrderPreviewResponse", description = "주문 미리보기 응답")
public record OrderPreviewResponse(
        @Schema(description = "미리보기 항목들")
        List<OrderPreviewItem> items,

        @Schema(description = "금액 합계")
        OrderPreviewTotals totals,

        @Schema(description = "배송 정보")
        OrderPreviewDelivery delivery,

        @Schema(description = "지원 결제수단 목록")
        @JsonProperty("payment_methods") List<SimpleMethod> paymentMethods
) {
    @Schema(name = "OrderPreviewResponse.SimpleMethod", description = "간단 결제수단")
    public record SimpleMethod(
            @Schema(description = "결제수단 키", example = "easypay_toss") String key,
            @Schema(description = "결제수단 라벨", example = "토스페이") String label
    ){}
}
