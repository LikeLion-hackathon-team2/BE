package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.*;
import com.hackathon2_BE.pium.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventQueryService {

    private final EventRepository eventRepository;

    public MonthlyEventsResponse getMonthly(String yyyyMm, String tz) {
        ZoneId zone = (tz == null || tz.isBlank()) ? ZoneId.of("Asia/Seoul") : ZoneId.of(tz);
        YearMonth ym = YearMonth.parse(yyyyMm);
        ZonedDateTime zStart = ym.atDay(1).atStartOfDay(zone);
        ZonedDateTime zEnd = ym.plusMonths(1).atDay(1).atStartOfDay(zone);

        var rows = eventRepository.findMonth(zStart.toLocalDateTime(), zEnd.toLocalDateTime());

        Map<LocalDate, List<MonthlyEventItemDto>> grouped = new LinkedHashMap<>();
        for (MonthlyEventRow r : rows) {
            LocalDate date = r.getStartAt().atZone(zone).toLocalDate();
            grouped.computeIfAbsent(date, d -> new ArrayList<>())
                    .add(MonthlyEventItemDto.builder()
                            .eventId(r.getEventId())
                            .title(r.getTitle())
                            .description(r.getDescription())
                            .startAt(r.getStartAt())
                            .endAt(r.getEndAt())
                            .categoryName(r.getCategoryName())
                            .iconEmoji(r.getIconEmoji())
                            .iconImageUrl(r.getIconImageUrl())
                            .build());
        }

        var days = grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> MonthlyEventDayDto.builder()
                        .date(e.getKey())
                        .items(e.getValue())
                        .build())
                .collect(Collectors.toList());

        return MonthlyEventsResponse.builder()
                .month(yyyyMm)
                .timezone(zone.getId())
                .days(days)
                .build();
    }
}
