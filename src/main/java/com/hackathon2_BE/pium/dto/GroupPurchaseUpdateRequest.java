package com.hackathon2_BE.pium.dto;

import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchaseUpdateRequest {
    private Integer minParticipants;
    private Integer maxParticipants;
    private LocalDateTime applyDeadlineAt;
    private LocalDateTime desiredDeliveryAt;
    private String recipientName;
    private String recipientPhone;
    private String address;
    private GroupPurchaseStatus status;
}
