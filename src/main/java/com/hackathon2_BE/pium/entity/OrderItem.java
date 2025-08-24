package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // orders.order_id
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id")
        private Product product;
}
