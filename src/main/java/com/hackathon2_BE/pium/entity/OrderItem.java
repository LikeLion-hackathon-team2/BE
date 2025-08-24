package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "orderItemId")
    private Long orderItemId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_price")
    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")

    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, referencedColumnName = "orderId")     // FK → orders.orderId
    private Order order;

    @PrePersist @PreUpdate
    void calcTotal() {
        if (totalPrice == null && product != null && product.getPrice() != null && quantity != null) {
            totalPrice = product.getPrice() * quantity;
        }
    }

    // 필요하면 읽기용 헬퍼 제공
    public Long getProductId() {
        return product != null ? product.getId() : null; // Product는 field명이 id
    }

    public Long getOrderId() {
        // Order 엔티티의 PK 필드명이 orderId라서 게터도 getOrderId()
        return order != null ? order.getOrderId() : null;
    }
}
