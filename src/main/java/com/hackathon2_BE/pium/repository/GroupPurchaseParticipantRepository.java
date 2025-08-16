package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.GroupPurchaseParticipant;
import com.hackathon2_BE.pium.entity.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupPurchaseParticipantRepository extends JpaRepository<GroupPurchaseParticipant, Long> {

    boolean existsByGroupPurchase_IdAndUser_Id(Long groupPurchaseId, Long userId);

    long countByGroupPurchase_IdAndStatusIn(Long groupPurchaseId, List<ParticipantStatus> statuses);

    @Query("select coalesce(sum(p.quantity),0) from GroupPurchaseParticipant p where p.groupPurchase.id = :groupPurchaseId and p.status in :statuses")
    Long sumQuantityByGroupPurchaseIdAndStatusIn(@Param("groupPurchaseId") Long groupPurchaseId,
                                                 @Param("statuses") List<ParticipantStatus> statuses);

    void deleteByGroupPurchase_Id(Long groupPurchaseId);

    Optional<GroupPurchaseParticipant> findByGroupPurchase_IdAndUser_Id(Long groupPurchaseId, Long userId);
}
