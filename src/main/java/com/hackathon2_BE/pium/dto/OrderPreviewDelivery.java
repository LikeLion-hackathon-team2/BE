package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record OrderPreviewDelivery(
        @JsonProperty("requested_date") LocalDate requestedDate,
        @JsonProperty("earliest_available") LocalDate earliestAvailable,
        @JsonProperty("unavailable_dates") List<LocalDate> unavailableDates
) {
}
