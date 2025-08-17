package com.hackathon2_BE.pium.dto;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.hackathon2_BE.pium.entity.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private String info;
    private Long categoryId;
    private Long gradeId;
    private Freshness freshness;
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
    public static class Freshness {
            private Long grade_id;   // 저장된 등급값(1~3)
            private Integer grade;   // 동일 값(프론트 편의)
            private String label;    // "매우 신선" | "양호" | "판매임박"

            public static Freshness of(Long gradeId) {
                if (gradeId == null) return null;
                int g = gradeId.intValue();
                String label = switch (g) {
                    case 3 -> "매우 신선";
                    case 2 -> "양호";
                    case 1 -> "판매임박";
                    default -> null; // 범위 밖 값은 표시 X
                };
                return (label == null) ? null : Freshness.builder()
                        .grade_id(gradeId)
                        .grade(g)
                        .label(label)
                        .build();
            }
     }
}