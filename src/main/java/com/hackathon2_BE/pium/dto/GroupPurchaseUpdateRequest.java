package com.hackathon2_BE.pium.dto;

import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공동구매 수정 요청(부분 업데이트)")
public class GroupPurchaseUpdateRequest {

    @Schema(description = "최소 참여 인원", example = "4")
    private Integer minParticipants;

    @Schema(description = "최대 참여 인원", example = "12")
    private Integer maxParticipants;

    @Schema(description = "모집 마감 시각(로컬, ISO-8601)", example = "2025-09-02T23:59:00")
    private LocalDateTime applyDeadlineAt;

    @Schema(description = "희망 배송 시각(로컬, ISO-8601)", example = "2025-09-06T10:00:00")
    private LocalDateTime desiredDeliveryAt;

    @Schema(description = "수령인 이름", example = "홍길동")
    private String recipientName;

    @Schema(description = "수령인 연락처", example = "010-5678-1234")
    private String recipientPhone;

    @Schema(description = "배송지 주소", example = "서울특별시 송파구 중대로 45, 1203호")
    private String address;

    @Schema(
            description = "상태(RECRUITING | PAYING | SHIPPING)",
            example = "PAYING",
            implementation = GroupPurchaseStatus.class
    )
    private GroupPurchaseStatus status;
}
