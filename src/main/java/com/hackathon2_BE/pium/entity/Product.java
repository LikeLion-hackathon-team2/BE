package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long gradeId;

    // 카테고리(FK)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;
    private String info;

    private Integer price;
    private Integer stockQuantity;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = java.time.LocalDateTime.now();
    }
    
    @Column(name = "unit_label", length = 20)
    private String unitLabel;

    @Column(name = "presets_csv", length = 100)
    private String presetsCsv;

    @Column(name = "quantity_min")
    private Integer quantityMin;

    @Column(name = "quantity_max")
    private Integer quantityMax;

    @Column(name = "quantity_step")
    private Integer quantityStep;

    @Column(name = "image_main_url", length = 500)
    private String imageMainUrl;

    @Column(name = "shop_name", length = 100)
    private String shopName;
}
