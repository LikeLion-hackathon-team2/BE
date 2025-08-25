// src/main/java/com/hackathon2_BE/pium/entity/Product.java
package com.hackathon2_BE.pium.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@ToString
@Entity
@Table(name = "product")
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "gradeId")
    private Long gradeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    @JsonIgnore                 // 직렬화 시 카테고리 전체는 숨김(순환/과다데이터 방지)
    private Category category;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "info", length = 255)
    private String info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "fk_product_shop"))
    @ToString.Exclude
    @JsonIgnore                 // 직렬화 시 상점 전체는 숨김
    private Shop shop;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @JsonManagedReference       // images → product 역참조는 직렬화에서 제외됨
    private List<ProductImage> images = new ArrayList<>();

    private Integer price;

    @Column(name = "stockQuantity")
    private Integer stockQuantity;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

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

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** 메인 이미지 상대경로를 한 곳에서 계산 (엔티티 직렬화에 포함 X) */
    @JsonIgnore
    public String getEffectiveMainImageUrl() {
        if (imageMainUrl != null && !imageMainUrl.isBlank()) return imageMainUrl;
        if (images == null) return null;
        return images.stream()
                .filter(ProductImage::isMain)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }
}
