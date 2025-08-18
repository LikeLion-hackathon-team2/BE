package com.hackathon2_BE.pium.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private String info;
    private Long categoryId;
}