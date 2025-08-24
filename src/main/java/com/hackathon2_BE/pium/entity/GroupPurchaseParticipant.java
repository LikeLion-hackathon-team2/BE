package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "group_purchase_participant",
        uniqueConstraints = @UniqueConstraint(name = "uk_group_user", columnNames = {"group_id", "user_id"})
)
public class GroupPurchaseParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupPurchase groupPurchase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    private ParticipantRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private ParticipantStatus status;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

}