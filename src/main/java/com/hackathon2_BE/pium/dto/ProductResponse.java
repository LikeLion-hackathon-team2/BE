// src/main/java/com/hackathon2_BE/pium/dto/ProductResponse.java
package com.hackathon2_BE.pium.dto;

import com.hackathon2_BE.pium.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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

    @Schema(description = "메인 이미지 절대 URL", example = "http://43.201.84.186:8080/uploads/products/3/xxxx.jpg")
    private String imageUrl;

    /** 기본 매핑(이미지 제외) */
    public static ProductResponse from(Product p) {
        ProductResponse r = new ProductResponse();
        r.id = p.getId();
        r.name = p.getName();
        r.price = p.getPrice();
        r.stockQuantity = p.getStockQuantity();
        r.info = p.getInfo();
        r.categoryId = (p.getCategory() != null) ? p.getCategory().getId() : null;
        r.gradeId = p.getGradeId();
        r.freshness = Freshness.of(p.getGradeId());
        r.createdAt = (p.getCreatedAt() != null)
                ? p.getCreatedAt().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                : null;
        r.imageUrl = null;
        return r;
    }

    /** 이미지 URL 포함 매핑(상대경로 → 절대경로로 변환) */
    public static ProductResponse from(Product p, String baseUrl) {
        ProductResponse r = from(p);
        String rel = p.getEffectiveMainImageUrl();
        r.imageUrl = toAbsoluteUrl(baseUrl, rel);
        return r;
    }

    private static String toAbsoluteUrl(String base, String rel) {
        if (rel == null || rel.isBlank()) return null;
        if (rel.startsWith("http://") || rel.startsWith("https://")) return rel;
        if (base == null || base.isBlank()) return rel;
        boolean baseEndsSlash = base.endsWith("/");
        boolean relStartsSlash = rel.startsWith("/");
        if (baseEndsSlash && relStartsSlash) return base.substring(0, base.length() - 1) + rel;
        if (!baseEndsSlash && !relStartsSlash) return base + "/" + rel;
        return base + rel;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ProductResponse.Freshness", description = "신선도 표현")
    public static class Freshness {
        @Schema(description = "등급 ID(원본 값)", example = "3")
        private Long grade_id;

        @Schema(description = "등급 값(동일 값, 프론트 편의)", example = "3")
        private Integer grade;

        @Schema(description = "라벨", example = "매우 신선")
        private String label;

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
