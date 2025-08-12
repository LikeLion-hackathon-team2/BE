package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor

public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private Long userId;
    private Long gradeId;
    private Long categoryId;

    private String name;
    private String info;

    private Integer price;
    private Integer stockQuantity;

    private LocalDateTime createdAt;

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
}
