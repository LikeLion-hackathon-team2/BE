package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.MonthlyEventsResponse;
import com.hackathon2_BE.pium.service.EventQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Events", description = "이벤트 캘린더 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventQueryController {

    private final EventQueryService eventQueryService;

    @Operation(
            summary = "월간 이벤트 조회",
            description = "월(YYYY-MM)과 선택적인 타임존(tz, 예: Asia/Seoul)을 받아 월간 이벤트를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "month-ok", value = """
                            {
                              "month": "2025-09",
                              "timezone": "Asia/Seoul",
                              "days": [
                                {
                                  "date": "2025-09-05",
                                  "items": [
                                    {
                                      "eventId": 10,
                                      "title": "추석 사전 예약 시작",
                                      "description": "한가위 특가",
                                      "startAt": "2025-09-05T09:00:00",
                                      "endAt": "2025-09-05T18:00:00",
                                      "categoryName": "프로모션",
                                      "iconEmoji": "🎉",
                                      "iconImageUrl": null
                                    }
                                  ]
                                }
                              ]
                            }
                            """))
            )
    })
    @GetMapping("/month")
    public MonthlyEventsResponse getMonthly(
            @Parameter(description = "YYYY-MM 형식의 월", example = "2025-09")
            @RequestParam String month,
            @Parameter(description = "IANA 타임존", example = "Asia/Seoul")
            @RequestParam(required = false) String tz
    ) {
        return eventQueryService.getMonthly(month, tz);
    }
}
