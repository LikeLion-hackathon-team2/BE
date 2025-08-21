package com.hackathon2_BE.pium.dto;

import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchaseUpdateRequest {
    @Schema(example = "4")
    private Integer minParticipants;
    @Schema(example = "12")
    private Integer maxParticipants;
    @Schema(example = "2025-08-24T12:00:00")
    private LocalDateTime applyDeadlineAt;
    @Schema(example = "2025-08-28T10:00:00")
    private LocalDateTime desiredDeliveryAt;
    @Schema(example = "김철수")
    private String recipientName;
    @Schema(example = "010-9999-0000")
    private String recipientPhone;
    @Schema(example = "서울특별시 마포구 합정동 1-2")
    private String address;
    @Schema(allowableValues = {"RECRUITING","PAYING","SHIPPING"}, example = "PAYING")
    private GroupPurchaseStatus status;
}
