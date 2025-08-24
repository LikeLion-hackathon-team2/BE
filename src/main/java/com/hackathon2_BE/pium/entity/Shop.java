package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "shop",
    uniqueConstraints = @UniqueConstraint(name="uk_shop_owner", columnNames={"owner_id"})
)
public class Shop {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name; // 가게명

    @OneToOne
    @JoinColumn(name="owner_id", nullable=false, foreignKey=@ForeignKey(name="fk_shop_owner"))
    private User owner;

    @OneToOne(mappedBy="shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_account_id", foreignKey = @ForeignKey(name = "fk_shop_deposit_account"))
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
}
