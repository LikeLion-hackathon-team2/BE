package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.GroupPurchase;
import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {
    Page<GroupPurchase> findByStatus(GroupPurchaseStatus status, Pageable pageable);
}
