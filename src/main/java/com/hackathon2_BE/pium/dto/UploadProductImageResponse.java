package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(name = "UploadProductImageResponse", description = "상품 이미지 업로드 응답")
public class UploadProductImageResponse {
    @Schema(description = "업로드된 이미지 정보")
    private Image image;

    @Schema(description = "연결된 상품 정보")
    private ProductInfo product;

    // ================= Image =================
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "UploadProductImageResponse.Image", description = "이미지 정보")
    public static class Image {
        @Schema(description = "이미지 ID", example = "9001")
        private Long image_id;

        @Schema(description = "이미지 URL", example = "https://cdn.example.com/img/101_main.jpg")
        private String image_url;

        @Schema(description = "대표 이미지 여부", example = "true")
        @JsonProperty("is_main")
        private boolean is_main;

        @Schema(description = "AI 보정 적용 여부", example = "true")
        private boolean ai_processed;
    }

    // ================= Product =================
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "UploadProductImageResponse.ProductInfo", description = "상품 요약 정보")
    public static class ProductInfo {
        @Schema(description = "상품 ID", example = "101")
        private Long product_id;

        @Schema(description = "신선도 등급 ID", example = "3")
        private Long grade_id;

        @Schema(description = "신선도 결과 (AI가 반환한 grade + label)")
        private Freshness freshness;
    }

    // ================= Freshness =================
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "UploadProductImageResponse.Freshness", description = "신선도")
    public static class Freshness {
        @Schema(description = "등급 값", example = "3")
        private Long grade;

        @Schema(description = "라벨", example = "매우 신선")
        private String label;
    }
}
