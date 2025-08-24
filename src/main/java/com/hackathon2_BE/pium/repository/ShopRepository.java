package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean existsByOwnerId(Long ownerId);
    Optional<Shop> findByOwnerId(Long ownerId);
}
