package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.ApplyRequest;
import com.hackathon2_BE.pium.dto.GroupPurchaseCreateRequest;
import com.hackathon2_BE.pium.dto.GroupPurchaseDetailResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseListResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseUpdateRequest;
import com.hackathon2_BE.pium.entity.*;
import com.hackathon2_BE.pium.exception.BadRequestException;
import com.hackathon2_BE.pium.repository.GroupPurchaseParticipantRepository;
import com.hackathon2_BE.pium.repository.GroupPurchaseRepository;
import com.hackathon2_BE.pium.repository.ProductRepository;
import com.hackathon2_BE.pium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import com.hackathon2_BE.pium.dto.GroupPurchaseDetailViewResponse;



@Service
@Transactional
@RequiredArgsConstructor
public class GroupPurchaseService {

    private final GroupPurchaseRepository groupRepo;
    private final GroupPurchaseParticipantRepository partRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public Long create(Long leaderUserId, GroupPurchaseCreateRequest req) {
        if (req.getMinParticipants() > req.getMaxParticipants()) throw new BadRequestException("min>max");
        if (!req.getApplyDeadlineAt().isBefore(req.getDesiredDeliveryAt())) throw new BadRequestException("deadline>=delivery");
        User leader = userRepo.findById(leaderUserId).orElseThrow(() -> new BadRequestException("user"));
        Product product = productRepo.findById(req.getProductId()).orElseThrow(() -> new BadRequestException("product"));
        GroupPurchase gp = GroupPurchase.builder()
                .leader(leader)
                .product(product)
                .minParticipants(req.getMinParticipants())
                .maxParticipants(req.getMaxParticipants())
                .applyDeadlineAt(req.getApplyDeadlineAt())
                .desiredDeliveryAt(req.getDesiredDeliveryAt())
                .recipientName(req.getRecipientName())
                .recipientPhone(req.getRecipientPhone())
                .address(req.getAddress())
                .status(GroupPurchaseStatus.RECRUITING)
                .build();
        groupRepo.save(gp);
        int leaderQty = req.getLeaderQuantity() == null ? 0 : req.getLeaderQuantity();
        if (leaderQty < 0) throw new BadRequestException("leaderQty");
        GroupPurchaseParticipant leaderPart = GroupPurchaseParticipant.builder()
                .groupPurchase(gp)
                .user(leader)
                .quantity(leaderQty)
                .role(ParticipantRole.LEADER)
                .status(ParticipantStatus.APPLIED)
                .build();
        partRepo.save(leaderPart);
        return gp.getId();
    }

    @Transactional(readOnly = true)
    public Page<GroupPurchaseListResponse> list(GroupPurchaseStatus status, Pageable pageable) {
        Page<GroupPurchase> page = status == null ? groupRepo.findAll(pageable) : groupRepo.findByStatus(status, pageable);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시", Locale.KOREAN);
        return page.map(g -> {
            int cur = Math.toIntExact(partRepo.countByGroupPurchase_IdAndStatusIn(
                    g.getId(), List.of(ParticipantStatus.APPLIED, ParticipantStatus.CONFIRMED)));
            return GroupPurchaseListResponse.builder()
                    .id(g.getId())
                    .leaderMaskedName(mask(g.getLeader().getUsername()))
                    .address(g.getAddress())
                    .currentParticipants(cur)
                    .maxParticipants(g.getMaxParticipants())
                    .farmName(g.getProduct().getShopName())
                    .deliveryAtText(g.getDesiredDeliveryAt().format(fmt))
                    .imageUrl(g.getProduct().getImageMainUrl())
                    .build();
        });
    }

    @Transactional(readOnly = true)
    public GroupPurchaseDetailResponse get(Long id) {
        GroupPurchase g = groupRepo.findById(id).orElseThrow(() -> new BadRequestException("group"));
        int cur = Math.toIntExact(partRepo.countByGroupPurchase_IdAndStatusIn(g.getId(), List.of(ParticipantStatus.APPLIED, ParticipantStatus.CONFIRMED)));
        int qty = safeSumQuantity(g.getId());
        Product p = g.getProduct();
        return GroupPurchaseDetailResponse.builder()
                .id(g.getId())
                .leaderName(g.getLeader().getUsername())
                .productId(p.getId())
                .productName(p.getName())
                .price(p.getPrice())
                .imageUrl(p.getImageMainUrl())
                .farmName(p.getShopName())
                .applyDeadlineAt(g.getApplyDeadlineAt())
                .desiredDeliveryAt(g.getDesiredDeliveryAt())
                .address(g.getAddress())
                .currentParticipants(cur)
                .minParticipants(g.getMinParticipants())
                .maxParticipants(g.getMaxParticipants())
                .totalQuantity(qty)
                .status(g.getStatus().name())
                .build();
    }

    public Long apply(Long groupId, Long userId, ApplyRequest req) {
        GroupPurchase g = groupRepo.findById(groupId).orElseThrow(() -> new BadRequestException("group"));
        if (!g.getStatus().equals(GroupPurchaseStatus.RECRUITING)) throw new BadRequestException("status");
        if (!LocalDateTime.now().isBefore(g.getApplyDeadlineAt())) throw new BadRequestException("deadline");
        int cur = Math.toIntExact(partRepo.countByGroupPurchase_IdAndStatusIn(groupId, List.of(ParticipantStatus.APPLIED, ParticipantStatus.CONFIRMED)));
        if (cur >= g.getMaxParticipants()) throw new BadRequestException("full");
        if (partRepo.existsByGroupPurchase_IdAndUser_Id(groupId, userId)) throw new BadRequestException("dup");
        if (req.getQuantity() == null || req.getQuantity() <= 0) throw new BadRequestException("qty");
        User user = userRepo.findById(userId).orElseThrow(() -> new BadRequestException("user"));
        GroupPurchaseParticipant p = GroupPurchaseParticipant.builder()
                .groupPurchase(g)
                .user(user)
                .quantity(req.getQuantity())
                .role(ParticipantRole.MEMBER)
                .status(ParticipantStatus.APPLIED)
                .build();
        partRepo.save(p);
        cur = Math.toIntExact(partRepo.countByGroupPurchase_IdAndStatusIn(groupId, List.of(ParticipantStatus.APPLIED, ParticipantStatus.CONFIRMED)));
        if (cur >= g.getMaxParticipants()) g.setStatus(GroupPurchaseStatus.FULL);
        return p.getId();
    }

    public void cancel(Long groupId, Long userId) {
        GroupPurchase g = groupRepo.findById(groupId).orElseThrow(() -> new BadRequestException("group"));
        if (!LocalDateTime.now().isBefore(g.getApplyDeadlineAt())) throw new BadRequestException("deadline");
        GroupPurchaseParticipant p = partRepo.findByGroupPurchase_IdAndUser_Id(groupId, userId)
                .orElseThrow(() -> new BadRequestException("notfound"));
        if (p.getRole() == ParticipantRole.LEADER) throw new BadRequestException("leader");
        if (p.getStatus() == ParticipantStatus.CANCELED) return;
        p.setStatus(ParticipantStatus.CANCELED);
        int cur = Math.toIntExact(partRepo.countByGroupPurchase_IdAndStatusIn(groupId, List.of(ParticipantStatus.APPLIED, ParticipantStatus.CONFIRMED)));
        if (g.getStatus() == GroupPurchaseStatus.FULL && cur < g.getMaxParticipants()) g.setStatus(GroupPurchaseStatus.RECRUITING);
    }

    public void update(Long leaderUserId, Long groupId, GroupPurchaseUpdateRequest req) {
        GroupPurchase g = groupRepo.findById(groupId).orElseThrow(() -> new BadRequestException("group"));
        if (!g.getLeader().getId().equals(leaderUserId)) throw new BadRequestException("forbidden");
        Integer minP = req.getMinParticipants() != null ? req.getMinParticipants() : g.getMinParticipants();
        Integer maxP = req.getMaxParticipants() != null ? req.getMaxParticipants() : g.getMaxParticipants();
        if (minP > maxP) throw new BadRequestException("min>max");
        g.setMinParticipants(minP);
        g.setMaxParticipants(maxP);
        if (req.getApplyDeadlineAt() != null) g.setApplyDeadlineAt(req.getApplyDeadlineAt());
        if (req.getDesiredDeliveryAt() != null) g.setDesiredDeliveryAt(req.getDesiredDeliveryAt());
        if (!g.getApplyDeadlineAt().isBefore(g.getDesiredDeliveryAt())) throw new BadRequestException("deadline>=delivery");
        if (req.getRecipientName() != null) g.setRecipientName(req.getRecipientName());
        if (req.getRecipientPhone() != null) g.setRecipientPhone(req.getRecipientPhone());
        if (req.getAddress() != null) g.setAddress(req.getAddress());
        if (req.getStatus() != null) g.setStatus(req.getStatus());
    }

    public void delete(Long leaderUserId, Long groupId) {
        GroupPurchase g = groupRepo.findById(groupId).orElseThrow(() -> new BadRequestException("group"));
        if (!g.getLeader().getId().equals(leaderUserId)) throw new BadRequestException("forbidden");
        partRepo.deleteByGroupPurchase_Id(groupId);
        groupRepo.delete(g);
    }

    private String mask(String s) {
        if (s == null || s.isBlank()) return s;
        if (s.length() == 1) return s;
        if (s.length() == 2) return s.charAt(0) + "*";
        return s.charAt(0) + "*" + s.substring(2);
    }

    private int safeSumQuantity(Long groupId) {
        try {
            Long v = partRepo.sumQuantityByGroupPurchaseIdAndStatusIn(groupId, List.of(ParticipantStatus.APPLIED, ParticipantStatus.CONFIRMED));
            return v == null ? 0 : v.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    @Transactional(readOnly = true)
    public GroupPurchaseDetailViewResponse getView(Long id) {
        GroupPurchase g = groupRepo.findById(id).orElseThrow(() -> new BadRequestException("group"));
        int cur = Math.toIntExact(
                partRepo.countByGroupPurchase_IdAndStatusIn(
                        g.getId(), List.of(ParticipantStatus.APPLIED, ParticipantStatus.CONFIRMED)));

        Product p = g.getProduct();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시", Locale.KOREAN);
        String priceText = String.format("%,d원", p.getPrice());

        return GroupPurchaseDetailViewResponse.builder()
                .id(g.getId())
                .imageUrl(p.getImageMainUrl())
                .farmName(p.getShopName())
                .productName(p.getName())
                .priceText(priceText)
                .address(g.getAddress())
                .applyDeadlineText(g.getApplyDeadlineAt().format(fmt))
                .desiredDeliveryText(g.getDesiredDeliveryAt().format(fmt))
                .currentParticipants(cur)
                .minParticipants(g.getMinParticipants())
                .maxParticipants(g.getMaxParticipants())
                .status(g.getStatus().name())
                .build();
    }
}

