package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.MonthlyEventsResponse;
import com.hackathon2_BE.pium.service.EventQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventQueryController {

    private final EventQueryService eventQueryService;

    @GetMapping("/month")
    public MonthlyEventsResponse getMonthly(
            @RequestParam String month,
            @RequestParam(required = false) String tz
    ) {
        return eventQueryService.getMonthly(month, tz);
    }
}
