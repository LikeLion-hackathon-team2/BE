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
        r.createdAt = (p.getCreatedAt() != null)
                ? p.getCreatedAt().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                : null;
        return r;
    }
}