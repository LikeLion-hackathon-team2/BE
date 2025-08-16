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
public class GroupPurchaseCreateRequest {
    private Long productId;
    private Integer leaderQuantity;
    private Integer minParticipants;
    private Integer maxParticipants;
    private LocalDateTime applyDeadlineAt;
    private LocalDateTime desiredDeliveryAt;
    private String recipientName;
    private String recipientPhone;
    private String address;
}
