package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id", nullable = false)
    private User consumer;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDate deliveryDate;
    private String deliveryAddress;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_type")
    private PurchaseType purchaseType;

    private String receiverName;
    private String receiverPhone;
    private Integer totalProductsPrice;
    private Integer shippingFee;
    private Integer grandTotal;

    public Shop getShop() {
        if (orderItems == null) return null;
        for (OrderItem oi : orderItems) {
            if (oi == null) continue;
            Product p = oi.getProduct();
            if (p != null && p.getShop() != null) {
                return p.getShop();
            }
        }
        return null;
    }
}
