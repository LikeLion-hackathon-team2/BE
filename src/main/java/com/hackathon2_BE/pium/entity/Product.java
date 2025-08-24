package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {

    // DB 컬럼: productId (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // DB 컬럼: userId
    @Column(name = "userId")
    private Long userId;

    // DB 컬럼: gradeId
    @Column(name = "gradeId")
    private Long gradeId;

    // DB 컬럼: categoryId (FK 값 저장, 카테고리 PK는 category.category_id)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "categoryId")
    private Category category;

    // DB 컬럼: name
    @Column(name = "name", length = 255)
    private String name;

    // DB 컬럼: info
    @Column(name = "info", length = 255)
    private String info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "fk_product_shop"))
    private Shop shop;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    private Integer price;

    // DB 컬럼: stockQuantity
    @Column(name = "stockQuantity")
    private Integer stockQuantity;

    // DB 컬럼: createdAt
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    // DB 컬럼: unit_label (snake_case)
    @Column(name = "unit_label", length = 20)
    private String unitLabel;

    // DB 컬럼: presets_csv (snake_case)
    @Column(name = "presets_csv", length = 100)
    private String presetsCsv;

    // DB 컬럼: quantity_min / quantity_max / quantity_step (snake_case)
    @Column(name = "quantity_min")
    private Integer quantityMin;

    @Column(name = "quantity_max")
    private Integer quantityMax;

    @Column(name = "quantity_step")
    private Integer quantityStep;

    // DB 컬럼: image_main_url (snake_case)
    @Column(name = "image_main_url", length = 500)
    private String imageMainUrl;

    // DB 컬럼: shop_name (snake_case)
    @Column(name = "shop_name", length = 100)
    private String shopName;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}
