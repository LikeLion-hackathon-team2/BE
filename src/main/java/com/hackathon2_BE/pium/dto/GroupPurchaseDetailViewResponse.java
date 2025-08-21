package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공동구매 상세 뷰 응답(문자열 가공 포함)")
public class GroupPurchaseDetailViewResponse {

    @Schema(description = "그룹 ID", example = "1001")
    private Long id;

    @Schema(description = "이미지 URL", example = "https://cdn.example.com/images/101-main.jpg")
    private String imageUrl;

    @Schema(description = "농가/상점명", example = "청송과수원")
    private String farmName;

    @Schema(description = "상품명", example = "샤인머스캣 2kg")
    private String productName;

    @Schema(description = "가격 문자열", example = "38,000원")
    private String priceText;

    @Schema(description = "배송지 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "모집 마감 텍스트", example = "2025년 9월 1일 오후 11시")
    private String applyDeadlineText;

    @Schema(description = "희망 배송일 텍스트", example = "2025년 9월 5일 오전 10시")
    private String desiredDeliveryText;

    @Schema(description = "현재 참여 인원", example = "5")
    private int currentParticipants;

    @Schema(description = "최소 참여 인원", example = "3")
    private int minParticipants;

    @Schema(description = "최대 참여 인원", example = "10")
    private int maxParticipants;

    @Schema(description = "상태(RECRUITING|PAYING|SHIPPING)", example = "RECRUITING")
    private String status;
}
