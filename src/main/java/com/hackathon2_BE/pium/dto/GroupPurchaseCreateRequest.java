package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchaseCreateRequest {
    @Schema(example = "101")
    private Long productId;
    @Schema(example = "1")
    private Integer leaderQuantity;
    @Schema(example = "3")
    private Integer minParticipants;
    @Schema(example = "10")
    private Integer maxParticipants;
    @Schema(example = "2025-08-23T18:00:00")
    private LocalDateTime applyDeadlineAt;
    @Schema(example = "2025-08-26T10:00:00")
    private LocalDateTime desiredDeliveryAt;
    @Schema(example = "홍길동")
    private String recipientName;
    @Schema(example = "010-1234-5678")
    private String recipientPhone;
    @Schema(example = "서울특별시 강남구 테헤란로 123")
    private String address;
}
