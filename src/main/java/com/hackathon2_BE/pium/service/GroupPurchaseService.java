package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.ApplyRequest;
import com.hackathon2_BE.pium.dto.GroupPurchaseCreateRequest;
import com.hackathon2_BE.pium.dto.GroupPurchaseDetailResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseDetailViewResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseListResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseUpdateRequest;
import com.hackathon2_BE.pium.entity.GroupPurchase;
import com.hackathon2_BE.pium.entity.GroupPurchaseParticipant;
import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import com.hackathon2_BE.pium.entity.ParticipantRole;
import com.hackathon2_BE.pium.entity.ParticipantStatus;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.entity.User;
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
        Page<GroupPurchase> page = (status == null)
                ? groupRepo.findAll(pageable)
                : groupRepo.findByStatus(status, pageable);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시", Locale.KOREAN);

        return page.map(g -> {
            int cur = Math.toIntExact(
                    partRepo.countByGroupPurchase_IdAndStatusIn(
                            g.getId(),
                            List.of(ParticipantStatus.APPLIED, ParticipantStatus.PAID)
                    )
            );

            Product p = g.getProduct();
            Integer price = p.getPrice();
            String priceText = (price != null) ? String.format("%,d원", price) : null;

            return GroupPurchaseListResponse.builder()
                    .id(g.getId())
                    .leaderMaskedName(mask(g.getLeader().getUsername()))
                    .address(g.getAddress())
                    .currentParticipants(cur)
                    .maxParticipants(g.getMaxParticipants())
                    .farmName(p.getShopName())
                    .productName(p.getName())
                    .deliveryAtText(g.getDesiredDeliveryAt().format(fmt))
                    .imageUrl(p.getImageMainUrl())
                    .price(price)
                    .priceText(priceText)
                    .build();
        });
    }

    @Transactional(readOnly = true)
    public GroupPurchaseDetailResponse get(Long id) {
        GroupPurchase g = groupRepo.findById(id).orElseThrow(() -> new BadRequestException("group"));
        int cur = Math.toIntExact(partRepo.countByGroupPurchase_IdAndStatusIn(
                g.getId(), List.of(ParticipantStatus.APPLIED, ParticipantStatus.PAID)));
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
        if (g.getStatus() != GroupPurchaseStatus.RECRUITING) throw new BadRequestException("status");
        if (!LocalDateTime.now().isBefore(g.getApplyDeadlineAt())) throw new BadRequestException("deadline");
        int cur = Math.toIntExact(partRepo.countByGroupPurchase_IdAndStatusIn(
                groupId, List.of(ParticipantStatus.APPLIED, ParticipantStatus.PAID)));
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
    }

    public void update(Long leaderUserId, Long groupId, GroupPurchaseUpdateRequest req) {
        GroupPurchase g = groupRepo.findById(groupId).orElseThrow(() -> new BadRequestException("group"));
        if (!g.getLeader().getId().equals(leaderUserId)) throw new BadRequestException("forbidden");

        Integer minP = (req.getMinParticipants() != null) ? req.getMinParticipants() : g.getMinParticipants();
        Integer maxP = (req.getMaxParticipants() != null) ? req.getMaxParticipants() : g.getMaxParticipants();
        if (minP > maxP) throw new BadRequestException("min>max");

        int current = activeCount(groupId);
        if (maxP < current) throw new BadRequestException("max<current");

        g.setMinParticipants(minP);
        g.setMaxParticipants(maxP);
        if (req.getApplyDeadlineAt() != null) g.setApplyDeadlineAt(req.getApplyDeadlineAt());
        if (req.getDesiredDeliveryAt() != null) g.setDesiredDeliveryAt(req.getDesiredDeliveryAt());
        if (!g.getApplyDeadlineAt().isBefore(g.getDesiredDeliveryAt())) throw new BadRequestException("deadline>=delivery");
        if (req.getRecipientName() != null) g.setRecipientName(req.getRecipientName());
        if (req.getRecipientPhone() != null) g.setRecipientPhone(req.getRecipientPhone());
        if (req.getAddress() != null) g.setAddress(req.getAddress());

        if (req.getStatus() != null) {
            GroupPurchaseStatus next = req.getStatus();
            if (!canTransit(g.getStatus(), next)) throw new BadRequestException("illegal_transition");
            if (next == GroupPurchaseStatus.PAYING) {
                if (!(LocalDateTime.now().isAfter(g.getApplyDeadlineAt())
                        && activeCount(g.getId()) >= g.getMinParticipants())) {
                    throw new BadRequestException("cannot_pay_phase");
                }
            }
            if (next == GroupPurchaseStatus.SHIPPING) {
                if (!allPaid(g.getId())) throw new BadRequestException("not_all_paid");
            }
            g.setStatus(next);
        }
    }

    public void delete(Long leaderUserId, Long groupId) {
        GroupPurchase g = groupRepo.findById(groupId).orElseThrow(() -> new BadRequestException("group"));
        if (!g.getLeader().getId().equals(leaderUserId)) throw new BadRequestException("forbidden");
        partRepo.deleteByGroupPurchase_Id(groupId);
        groupRepo.delete(g);
    }

    public void pay(Long groupId, Long userId) {
        GroupPurchase g = groupRepo.findById(groupId).orElseThrow(() -> new BadRequestException("group"));
        if (g.getStatus() != GroupPurchaseStatus.PAYING) throw new BadRequestException("status");
        GroupPurchaseParticipant p = partRepo.findByGroupPurchase_IdAndUser_Id(groupId, userId)
                .orElseThrow(() -> new BadRequestException("notfound"));
        if (p.getStatus() == ParticipantStatus.CANCELED) throw new BadRequestException("canceled");
        if (p.getStatus() == ParticipantStatus.PAID) return;
        p.setStatus(ParticipantStatus.PAID);
        if (allPaid(groupId)) g.setStatus(GroupPurchaseStatus.SHIPPING);
    }

    private String mask(String s) {
        if (s == null || s.isBlank()) return s;
        if (s.length() == 1) return s;
        if (s.length() == 2) return s.charAt(0) + "*";
        return s.charAt(0) + "*" + s.substring(2);
    }

    private int safeSumQuantity(Long groupId) {
        try {
            Long v = partRepo.sumQuantityByGroupPurchaseIdAndStatusIn(
                    groupId, List.of(ParticipantStatus.APPLIED, ParticipantStatus.PAID));
            return (v == null) ? 0 : v.intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private int activeCount(Long groupId) {
        return Math.toIntExact(
                partRepo.countByGroupPurchase_IdAndStatusIn(
                        groupId, List.of(ParticipantStatus.APPLIED, ParticipantStatus.PAID)
                )
        );
    }

    private boolean allPaid(Long groupId) {
        long total = partRepo.countByGroupPurchase_IdAndStatusIn(
                groupId, List.of(ParticipantStatus.APPLIED, ParticipantStatus.PAID));
        long paid = partRepo.countByGroupPurchase_IdAndStatusIn(
                groupId, List.of(ParticipantStatus.PAID));
        return total > 0 && total == paid;
    }

    private boolean canTransit(GroupPurchaseStatus cur, GroupPurchaseStatus next) {
        if (cur == next) return true;
        if (cur == GroupPurchaseStatus.RECRUITING && next == GroupPurchaseStatus.PAYING) return true;
        if (cur == GroupPurchaseStatus.PAYING && next == GroupPurchaseStatus.SHIPPING) return true;
        return false;
    }

    @Transactional(readOnly = true)
    public GroupPurchaseDetailViewResponse getView(Long id) {
        GroupPurchase g = groupRepo.findById(id).orElseThrow(() -> new BadRequestException("group"));
        int cur = Math.toIntExact(partRepo.countByGroupPurchase_IdAndStatusIn(
                g.getId(), List.of(ParticipantStatus.APPLIED, ParticipantStatus.PAID)));
        Product p = g.getProduct();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시", Locale.KOREAN);
        Integer price = p.getPrice();
        String priceText = (price != null) ? String.format("%,d원", price) : null;

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
