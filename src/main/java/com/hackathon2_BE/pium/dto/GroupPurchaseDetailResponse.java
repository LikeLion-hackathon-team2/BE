package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchaseDetailResponse {
    @Schema(example = "33")
    private Long id;
    @Schema(example = "leaderA")
    private String leaderName;
    @Schema(example = "101")
    private Long productId;
    @Schema(example = "테스트 사과 세트")
    private String productName;
    @Schema(example = "12000")
    private Integer price;
    @Schema(example = "https://cdn.example.com/images/101-main.jpg")
    private String imageUrl;
    @Schema(example = "상큼농원")
    private String farmName;
    @Schema(example = "2025-08-23T18:00:00")
    private LocalDateTime applyDeadlineAt;
    @Schema(example = "2025-08-26T10:00:00")
    private LocalDateTime desiredDeliveryAt;
    @Schema(example = "서울특별시 강남구 테헤란로 123")
    private String address;
    @Schema(example = "3")
    private int currentParticipants;
    @Schema(example = "3")
    private int minParticipants;
    @Schema(example = "10")
    private int maxParticipants;
    @Schema(example = "5")
    private int totalQuantity;
    @Schema(example = "PAYING")
    private String status;
}
