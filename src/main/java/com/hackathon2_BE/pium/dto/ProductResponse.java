package com.hackathon2_BE.pium.dto;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.hackathon2_BE.pium.entity.Product;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ProductResponse", description = "상품 응답")
public class ProductResponse {

    @Schema(description = "상품 ID", example = "101")
    private Long id;

    @Schema(description = "상품명", example = "청송 사과 5kg")
    private String name;

    @Schema(description = "가격(원)", example = "16000")
    private Integer price;

    @Schema(description = "재고 수량", example = "120")
    private Integer stockQuantity;

    @Schema(description = "상품 설명", example = "아삭하고 달콤한 청송 사과")
    private String info;

    @Schema(description = "카테고리 ID", example = "5")
    private Long categoryId;

    @Schema(description = "신선도 등급 ID(1~3)", example = "3")
    private Long gradeId;

    @Schema(description = "신선도 정보")
    private Freshness freshness;

    @Schema(description = "생성 시각(ISO-8601 UTC)", example = "2025-08-22T00:00:00Z")
    private String createdAt;

    public static ProductResponse from(Product p) {
        ProductResponse r = new ProductResponse();
        r.id = p.getId();
        r.name = p.getName();
        r.price = p.getPrice();
        r.stockQuantity = p.getStockQuantity();
        r.info = p.getInfo();
        r.categoryId = (p.getCategory() != null) ? p.getCategory().getId() : null;
        r.gradeId = p.getGradeId();
        r.setFreshness(Freshness.of(p.getGradeId()));
        r.createdAt = (p.getCreatedAt() != null)
                ? p.getCreatedAt().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                : null;
        return r;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ProductResponse.Freshness", description = "신선도 표현")
    public static class Freshness {

        @Schema(description = "등급 ID(원본 값)", example = "3")
        private Long grade_id;   // 저장된 등급값(1~3)

        @Schema(description = "등급 값(동일 값, 프론트 편의)", example = "3")
        private Integer grade;

        @Schema(description = "라벨", example = "매우 신선")
        private String label;    // "매우 신선" | "양호" | "판매임박"

        public static Freshness of(Long gradeId) {
            if (gradeId == null) return null;
            int g = gradeId.intValue();
            String label = switch (g) {
                case 3 -> "매우 신선";
                case 2 -> "양호";
                case 1 -> "판매임박";
                default -> null;
            };
            return (label == null) ? null : Freshness.builder()
                    .grade_id(gradeId)
                    .grade(g)
                    .label(label)
                    .build();
        }
    }
}
