package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.*;
import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import com.hackathon2_BE.pium.security.CustomUserDetails;
import com.hackathon2_BE.pium.service.GroupPurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "GroupPurchases", description = "공동구매 API")
@RestController
@RequestMapping("/api/group-purchases")
public class GroupPurchaseController {

    private final GroupPurchaseService service;
    public GroupPurchaseController(GroupPurchaseService service) { this.service = service; }

    @Operation(summary = "공동구매 생성")
    @PostMapping
    public Long create(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody GroupPurchaseCreateRequest req
    ) {
        return service.create(me.getId(), req);
    }

    @Operation(summary = "공동구매 목록 조회")
    @GetMapping
    public Page<GroupPurchaseListResponse> list(
            @RequestParam(required = false) GroupPurchaseStatus status,
            Pageable pageable
    ) {
        return service.list(status, pageable);
    }

    @Operation(summary = "공동구매 상세 조회")
    @GetMapping("/{id}")
    public GroupPurchaseDetailResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "공동구매 상세(뷰) 조회")
    @GetMapping("/{id}/view")
    public GroupPurchaseDetailViewResponse getView(@PathVariable Long id) {
        return service.getView(id);
    }

    @Operation(summary = "공동구매 참여 신청")
    @PostMapping("/{id}/apply")
    public Long apply(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody ApplyRequest req
    ) {
        return service.apply(id, me.getId(), req);
    }

    @Operation(summary = "공동구매 참여 취소")
    @DeleteMapping("/{id}/apply")
    public void cancel(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me
    ) {
        service.cancel(id, me.getId());
    }

    @Operation(summary = "입금 완료 처리")
    @PostMapping("/{id}/pay")
    public void pay(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me
    ) {
        service.pay(id, me.getId());
    }

    @Operation(summary = "공동구매 수정")
    @PatchMapping("/{id}")
    public void update(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody GroupPurchaseUpdateRequest req
    ) {
        service.update(me.getId(), id, req);
    }

    @Operation(summary = "공동구매 삭제")
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me
    ) {
        service.delete(me.getId(), id);
    }
}
