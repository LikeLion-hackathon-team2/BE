package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartItemIdInAndUserId(List<Long> cartItemIds, Long userId);
    Optional<CartItem> findByCartItemIdAndUserId(Long cartItemId, Long userId);
    void deleteByCartItemIdAndUserId(Long cartItemId, Long userId);
}
