package com.hackathon2_BE.pium.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchaseDetailViewResponse {
    private Long id;
    private String imageUrl;
    private String farmName;
    private String productName;
    private String priceText;
    private String address;
    private String applyDeadlineText;
    private String desiredDeliveryText;
    private int currentParticipants;
    private int minParticipants;
    private int maxParticipants;
    private String status;
}
