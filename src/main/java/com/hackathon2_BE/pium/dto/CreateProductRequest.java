package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "CreateProductRequest", description = "상품 생성 요청")
public class CreateProductRequest {

    @Schema(description = "상품명", example = "무농약 대파 1kg")
    private String name;

    @Schema(description = "가격(원)", example = "7800")
    private Integer price;

    @Schema(description = "재고 수량", example = "120")
    private Integer stockQuantity;

    @Schema(description = "상품 설명", example = "전남 해남산, 산지 직송")
    private String info;

    @Schema(description = "카테고리 ID", example = "3")
    private Long categoryId;
}
