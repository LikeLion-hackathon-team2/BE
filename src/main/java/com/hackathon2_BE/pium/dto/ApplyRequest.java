package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공동구매 참여 신청 요청")
public class ApplyRequest {

    @Schema(description = "신청 수량", example = "3")
    private Integer quantity;
}
