package com.hackathon2_BE.pium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchaseDetailResponse {
    private Long id;
    private String leaderName;
    private Long productId;
    private String productName;
    private Integer price;
    private String imageUrl;
    private String farmName;
    private LocalDateTime applyDeadlineAt;
    private LocalDateTime desiredDeliveryAt;
    private String address;
    private int currentParticipants;
    private int minParticipants;
    private int maxParticipants;
    private int totalQuantity;
    private String status;
}
