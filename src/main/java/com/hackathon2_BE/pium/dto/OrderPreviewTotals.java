package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderPreviewTotals(Integer products, Integer shipping, @JsonProperty("grand_total") Integer grandTotal) {
}
