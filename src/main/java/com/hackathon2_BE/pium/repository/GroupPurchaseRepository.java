package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.GroupPurchase;
import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {
    Page<GroupPurchase> findByStatus(GroupPurchaseStatus status, Pageable pageable);

    // 상점-오너 구조 기준: 내가 개설한 공구
    @Query("""
        select distinct gp
          from GroupPurchase gp
          join fetch gp.product prod
          join fetch prod.shop s
          left join fetch s.depositAccount da
          left join fetch prod.images imgs
         where s.owner.id = :userId
         order by gp.createdAt desc
    """)
    List<GroupPurchase> findOwnedByUserId(Long userId);
}
