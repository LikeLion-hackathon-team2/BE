package com.hackathon2_BE.pium.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionResponse {
    private Long productId;
    private String unitLabel;
    private Integer unitPrice;
    private Integer stockRemaining;
    private List<Integer> presets;
    private Map<String, Integer> quantity;
}
