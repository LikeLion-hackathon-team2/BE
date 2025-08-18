package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.dto.MonthlyEventRow;
import com.hackathon2_BE.pium.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
      select new com.hackathon2_BE.pium.dto.MonthlyEventRow(
        e.eventId, e.title, e.description, e.startAt, e.endAt,
        c.name, c.iconEmoji, c.iconImageUrl
      )
      from Event e
      join e.category c
      where e.startAt >= :from and e.startAt < :to
      order by e.startAt asc, e.eventId asc
    """)
    List<MonthlyEventRow> findMonth(@Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to);
}
