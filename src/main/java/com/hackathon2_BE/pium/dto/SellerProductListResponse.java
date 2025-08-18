package com.hackathon2_BE.pium.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SellerProductListResponse {
    private List<Item> items;
    private Pagination pagination;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Item {
        private Long product_id;
        private String name;
        private Integer price;
        private Integer stock_quantity;
        private String status;
        private Long category_id;
        private String main_image_url;
        private Freshness freshness;
        private LocalDateTime created_at;
        private LocalDateTime updated_at;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Freshness {
        private Long grade_id;
        private Integer grade;
        private String label;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Pagination {
        private int page;
        private int size;
        private long total;
    }
}
