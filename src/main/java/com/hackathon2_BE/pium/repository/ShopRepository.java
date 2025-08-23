package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean existsByOwnerId(Long ownerId);
}
