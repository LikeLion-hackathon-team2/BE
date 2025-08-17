package com.hackathon2_BE.pium.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UploadProductImageResponse {

    private Image image;
    private ProductInfo product;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Image {
        private Long image_id;
        private String image_url;
        @JsonProperty("is_main")
        private boolean is_main;
        private boolean ai_processed; // 지금은 항상 false
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ProductInfo {
        private Long product_id;
        private Long grade_id; // 신선도 분석 미적용: null 유지
        private Freshness freshness;  // 선택 필드: 없으면 null
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Freshness {
        private Integer grade;  // 1~3
        private String label;   // "판매임박" | "양호" | "매우 신선"
    }
}
