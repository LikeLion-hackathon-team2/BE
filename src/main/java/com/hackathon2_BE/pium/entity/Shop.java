package com.hackathon2_BE.pium.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
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
@Entity @Table(name = "shop",
    uniqueConstraints = @UniqueConstraint(name="uk_shop_owner", columnNames={"owner_id"})
)
public class Shop {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name; // 가게명

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id", nullable=false, foreignKey=@ForeignKey(name="fk_shop_owner"))
    private User owner;

    @OneToOne(mappedBy="shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DepositAccount depositAccount;

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

    // 편의 메서드
    public void setDepositAccount(DepositAccount account) {
        this.depositAccount = account;
        if (account != null) account.setShop(this);
    }

    public void setOwner(User owner) {
    this.owner = owner;
    if (owner != null && owner.getShop() != this) {
        owner.setShop(this);
    }
    }
}
