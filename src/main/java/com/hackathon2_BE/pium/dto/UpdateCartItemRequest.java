package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdateCartItemRequest", description = "장바구니 수량 수정 요청")
public record UpdateCartItemRequest(
        @Schema(description = "수정할 수량", example = "3")
        Integer quantity
) {}
