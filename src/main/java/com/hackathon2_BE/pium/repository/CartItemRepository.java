package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
