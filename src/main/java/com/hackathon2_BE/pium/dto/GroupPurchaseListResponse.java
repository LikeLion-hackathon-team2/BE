package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공동구매 목록 응답")
public class GroupPurchaseListResponse {

    @Schema(description = "그룹 ID", example = "1001")
    private Long id;

    @Schema(description = "리더 마스킹 이름", example = "김*동")
    private String leaderMaskedName;

    @Schema(description = "배송지 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "현재 참여 인원", example = "5")
    private int currentParticipants;

    @Schema(description = "최대 참여 인원", example = "10")
    private int maxParticipants;

    @Schema(description = "농가/상점명", example = "청송과수원")
    private String farmName;

    @Schema(description = "상품명", example = "청송 사과 5kg")
    private String productName;

    @Schema(description = "배송 예정 텍스트", example = "2025년 9월 5일 오전 10시")
    private String deliveryAtText;

    @Schema(description = "대표 이미지 URL", example = "https://cdn.example.com/images/101-main.jpg")
    private String imageUrl;

    @Schema(description = "판매가(원)", example = "38000")
    private Integer price;

    @Schema(description = "가격 문자열", example = "38,000원")
    private String priceText;
}
