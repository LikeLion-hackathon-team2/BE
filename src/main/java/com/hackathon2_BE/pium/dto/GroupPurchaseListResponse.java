package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchaseListResponse {
    @Schema(example = "33")
    private Long id;
    @Schema(example = "l*aderA")
    private String leaderMaskedName;
    @Schema(example = "서울특별시 강남구 테헤란로 123")
    private String address;
    @Schema(example = "3")
    private int currentParticipants;
    @Schema(example = "10")
    private int maxParticipants;
    @Schema(example = "상큼농원")
    private String farmName;
    @Schema(example = "2025년 8월 26일 오전 10시")
    private String deliveryAtText;
    @Schema(example = "https://cdn.example.com/images/101-main.jpg")
    private String imageUrl;
    @Schema(example = "12000")
    private Integer price;
    @Schema(example = "12,000원")
    private String priceText;
}
