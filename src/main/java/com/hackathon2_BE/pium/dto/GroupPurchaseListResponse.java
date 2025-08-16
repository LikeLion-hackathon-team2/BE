package com.hackathon2_BE.pium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchaseListResponse {
    private Long id;
    private String leaderMaskedName;
    private String address;
    private int currentParticipants;
    private int maxParticipants;
    private String farmName;
    private String deliveryAtText;
    private String imageUrl;
}
