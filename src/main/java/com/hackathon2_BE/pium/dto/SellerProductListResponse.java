package com.hackathon2_BE.pium.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(name = "SellerProductListResponse", description = "판매자 상품 목록 응답")
public class SellerProductListResponse {

    @Schema(description = "상품 아이템 목록")
    private List<Item> items;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "SellerProductListResponse.Item", description = "상품 아이템")
    public static class Item {
        @Schema(description = "상품 ID", example = "101")
        private Long product_id;

        @Schema(description = "상품명", example = "청송 사과 5kg")
        private String name;

        @Schema(description = "가격(원)", example = "28000")
        private Integer price;

        @Schema(description = "재고 수량", example = "120")
        private Integer stock_quantity;

        @Schema(description = "상태", example = "ACTIVE")
        private String status;

        @Schema(description = "카테고리 ID", example = "5")
        private Long category_id;

        @Schema(description = "대표 이미지 URL", example = "https://cdn.example.com/p/101_main.jpg")
        private String main_image_url;

        @Schema(description = "신선도")
        private Freshness freshness;

        @Schema(description = "생성 시각", example = "2025-08-22T10:00:00")
        private LocalDateTime created_at;

        @Schema(description = "수정 시각", example = "2025-08-23T09:12:34")
        private LocalDateTime updated_at;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "SellerProductListResponse.Freshness", description = "신선도 정보")
    public static class Freshness {
        @Schema(description = "등급 ID", example = "3")
        private Long grade_id;

        @Schema(description = "등급 값", example = "3")
        private Integer grade;

        @Schema(description = "라벨", example = "매우 신선")
        private String label;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "SellerProductListResponse.Pagination", description = "페이지네이션")
    public static class Pagination {
        @Schema(description = "페이지(0부터 시작)", example = "0")
        private int page;

        @Schema(description = "페이지 크기", example = "20")
        private int size;

        @Schema(description = "총 항목 수", example = "137")
        private long total;
    }
}
