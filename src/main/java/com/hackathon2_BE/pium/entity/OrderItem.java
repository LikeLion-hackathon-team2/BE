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
    @Column(name = "order_item_id") // PK + AUTO_INCREMENT
    private Long orderItemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, referencedColumnName = "productId") // FK → product.productId
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, referencedColumnName = "orderId")     // FK → orders.orderId
    private Order order;

    // 읽기용 헬퍼
    public Long getProductId() {
        return product != null ? product.getId() : null; // Product는 field명이 id
    }

    public Long getOrderId() {
        // Order 엔티티의 PK 필드명이 orderId라서 게터도 getOrderId()
        return order != null ? order.getOrderId() : null;
    }
}
