package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartItemIdInAndUserId(List<Long> ids, Long userId);
}
