package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.ApplyRequest;
import com.hackathon2_BE.pium.dto.GroupPurchaseCreateRequest;
import com.hackathon2_BE.pium.dto.GroupPurchaseDetailResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseDetailViewResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseListResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseUpdateRequest;
import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import com.hackathon2_BE.pium.service.GroupPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-purchases")
@RequiredArgsConstructor
public class GroupPurchaseController {

    private final GroupPurchaseService service;

    @PostMapping
    public Long create(@RequestHeader("X-USER-ID") Long leaderUserId, @RequestBody GroupPurchaseCreateRequest req) {
        return service.create(leaderUserId, req);
    }

    @GetMapping
    public Page<GroupPurchaseListResponse> list(@RequestParam(required = false) GroupPurchaseStatus status, Pageable pageable) {
        return service.list(status, pageable);
    }

    @GetMapping("/{id}")
    public GroupPurchaseDetailResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/{id}/view")
    public GroupPurchaseDetailViewResponse getView(@PathVariable Long id) {
        return service.getView(id);
    }

    @PostMapping("/{id}/apply")
    public Long apply(@PathVariable Long id, @RequestHeader("X-USER-ID") Long userId, @RequestBody ApplyRequest req) {
        return service.apply(id, userId, req);
    }

    @DeleteMapping("/{id}/apply")
    public void cancel(@PathVariable Long id, @RequestHeader("X-USER-ID") Long userId) {
        service.cancel(id, userId);
    }

    @PostMapping("/{id}/pay")
    public void pay(@PathVariable Long id, @RequestHeader("X-USER-ID") Long userId) {
        service.pay(id, userId);
    }

    @PatchMapping("/{id}")
    public void update(@PathVariable Long id, @RequestHeader("X-USER-ID") Long leaderUserId, @RequestBody GroupPurchaseUpdateRequest req) {
        service.update(leaderUserId, id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-USER-ID") Long leaderUserId) {
        service.delete(leaderUserId, id);
    }
}
