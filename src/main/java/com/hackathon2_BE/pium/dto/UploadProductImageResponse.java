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
        private boolean ai_processed;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ProductInfo {
        private Long product_id;
        private Long grade_id;
        private Freshness freshness;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Freshness {
        private Integer grade;
        private String label;
    }
}
