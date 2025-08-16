package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
    Optional<EventCategory> findByCode(String code);
}
