package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record OrderPreviewRequest(
        @JsonProperty("cart_item_ids") List<Long> cartItemIds,
        @JsonProperty("desired_delivery_date") LocalDate desiredDeliveryDate
) {
}
