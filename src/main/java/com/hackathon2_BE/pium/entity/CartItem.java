package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_item")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")      // PK + AUTO_INCREMENT (스키마와 동일)
    private Long cartItemId;

    @Column(name = "user_id", nullable = false)        // FK → users.id
    private Long userId;

    @Column(name = "product_id", nullable = false)     // FK → product.productId
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "subtotal", nullable = false)       // unit_price * quantity
    private Integer subtotal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}
