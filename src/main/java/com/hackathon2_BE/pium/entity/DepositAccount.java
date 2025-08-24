package com.hackathon2_BE.pium.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "deposit_account",
    uniqueConstraints = @UniqueConstraint(name="uk_deposit_account_shop", columnNames={"shop_id"})
)
public class DepositAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=40)
    private String bank;

    @Column(nullable=false, length=64)
    private String number; // 보관은 원문(또는 암호화). 응답에서만 마스킹.

    @Column(nullable=false, length=100)
    private String holder;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shop_id", nullable=false, foreignKey=@ForeignKey(name="fk_deposit_account_shop"))
    private Shop shop;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @Column(nullable=false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}