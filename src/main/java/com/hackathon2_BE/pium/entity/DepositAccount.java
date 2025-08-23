package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @OneToOne
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