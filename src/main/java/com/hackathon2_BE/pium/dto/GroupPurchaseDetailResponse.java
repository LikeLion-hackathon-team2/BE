package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공동구매 상세 응답")
public class GroupPurchaseDetailResponse {

    @Schema(description = "그룹 ID", example = "1001")
    private Long id;

    @Schema(description = "리더 이름", example = "김리더")
    private String leaderName;

    @Schema(description = "상품 ID", example = "101")
    private Long productId;

    @Schema(description = "상품명", example = "샤인머스캣 2kg")
    private String productName;

    @Schema(description = "판매가(원)", example = "38000")
    private Integer price;

    @Schema(description = "상품 대표 이미지 URL", example = "https://cdn.example.com/images/101-main.jpg")
    private String imageUrl;

    @Schema(description = "농가/상점명", example = "청송과수원")
    private String farmName;

    @Schema(description = "모집 마감 시각", example = "2025-09-01T23:59:00")
    private LocalDateTime applyDeadlineAt;

    @Schema(description = "희망 배송 시각", example = "2025-09-05T10:00:00")
    private LocalDateTime desiredDeliveryAt;

    @Schema(description = "배송지 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "현재 참여 인원", example = "5")
    private int currentParticipants;

    @Schema(description = "최소 참여 인원", example = "3")
    private int minParticipants;

    @Schema(description = "최대 참여 인원", example = "10")
    private int maxParticipants;

    @Schema(description = "총 주문 수량(모든 참가자 합계)", example = "12")
    private int totalQuantity;

    @Schema(description = "상태(RECRUITING|PAYING|SHIPPING)", example = "RECRUITING")
    private String status;
}
