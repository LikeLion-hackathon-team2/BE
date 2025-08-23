package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.GroupPurchaseParticipant;
import com.hackathon2_BE.pium.entity.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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

    // 내가 참여한 공동구매를 연관까지 한 번에 로드 (N+1 방지)
    @Query("""
    select distinct gpp
      from GroupPurchaseParticipant gpp
      join fetch gpp.groupPurchase gp
      join fetch gp.product prod
      join fetch prod.shop s
      left join fetch s.depositAccount da
      left join fetch prod.images imgs
     where gpp.user.id = :userId
     order by gp.createdAt desc
""")
    List<GroupPurchaseParticipant> findDeepByUserId(Long userId);

    // 공구ID 집합에 대한 참여 인원 수 집계 (필요 시 상태 필터 추가)
    @Query("""
        select gp.id as gpId, count(gpp2.id) as cnt
        from GroupPurchaseParticipant gpp2
        join gpp2.groupPurchase gp
        where gp.id in :gpIds
        group by gp.id
    """)
    List<Object[]> countByGroupPurchaseIds(Collection<Long> gpIds);
}
