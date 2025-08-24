package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DataGoKrBizStatusRequest {

    @JsonProperty("b_no")
    private List<String> bNo;
}
