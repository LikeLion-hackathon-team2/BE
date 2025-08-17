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

    @Column(name = "userId")
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

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;                        // 생성 시에도 updatedAt 채움
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();        // 엔티티 변경 시 자동 갱신
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
