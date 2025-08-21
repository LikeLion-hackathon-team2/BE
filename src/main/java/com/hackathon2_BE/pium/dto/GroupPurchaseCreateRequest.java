package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공동구매 생성 요청")
public class GroupPurchaseCreateRequest {

    @Schema(description = "상품 ID", example = "101")
    private Long productId;

    @Schema(description = "리더 본인 수량", example = "1")
    private Integer leaderQuantity;

    @Schema(description = "최소 참여 인원", example = "3")
    private Integer minParticipants;

    @Schema(description = "최대 참여 인원", example = "10")
    private Integer maxParticipants;

    @Schema(description = "모집 마감 시각(로컬, ISO-8601)", example = "2025-08-23T18:00:00")
    private LocalDateTime applyDeadlineAt;

    @Schema(description = "희망 배송 시각(로컬, ISO-8601)", example = "2025-08-26T10:00:00")
    private LocalDateTime desiredDeliveryAt;

    @Schema(description = "수령인 이름", example = "홍길동")
    private String recipientName;

    @Schema(description = "수령인 연락처", example = "010-1234-5678")
    private String recipientPhone;

    @Schema(description = "배송지 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;
}
