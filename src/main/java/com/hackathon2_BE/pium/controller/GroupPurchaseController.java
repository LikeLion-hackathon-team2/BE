package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.ApplyRequest;
import com.hackathon2_BE.pium.dto.GroupPurchaseCreateRequest;
import com.hackathon2_BE.pium.dto.GroupPurchaseDetailResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseDetailViewResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseListResponse;
import com.hackathon2_BE.pium.dto.GroupPurchaseUpdateRequest;
import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import com.hackathon2_BE.pium.security.CustomUserDetails;
import com.hackathon2_BE.pium.service.GroupPurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-purchases")
public class GroupPurchaseController {

    private final GroupPurchaseService service;

    public GroupPurchaseController(GroupPurchaseService service) {
        this.service = service;
    }

    @Operation(
            summary = "공동구매 생성",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "CreateGroupPurchase",
                                    value = "{\n" +
                                            "  \"productId\": 101,\n" +
                                            "  \"leaderQuantity\": 1,\n" +
                                            "  \"minParticipants\": 3,\n" +
                                            "  \"maxParticipants\": 10,\n" +
                                            "  \"applyDeadlineAt\": \"2025-08-23T18:00:00\",\n" +
                                            "  \"desiredDeliveryAt\": \"2025-08-26T10:00:00\",\n" +
                                            "  \"recipientName\": \"홍길동\",\n" +
                                            "  \"recipientPhone\": \"010-1234-5678\",\n" +
                                            "  \"address\": \"서울특별시 강남구 테헤란로 123\"\n" +
                                            "}"
                            )
                    )
            )
    )
    @PostMapping
    public Long create(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestBody GroupPurchaseCreateRequest req) {
        return service.create(me.getId(), req);
    }

    @Operation(summary = "공동구매 리스트 조회")
    @GetMapping
    public Page<GroupPurchaseListResponse> list(@RequestParam(required = false) GroupPurchaseStatus status,
                                                Pageable pageable) {
        return service.list(status, pageable);
    }

    @Operation(summary = "공동구매 상세 조회")
    @GetMapping("/{id}")
    public GroupPurchaseDetailResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "공동구매 상세 뷰")
    @GetMapping("/{id}/view")
    public GroupPurchaseDetailViewResponse getView(@PathVariable Long id) {
        return service.getView(id);
    }

    @Operation(
            summary = "공동구매 신청",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Apply",
                                    value = "{ \"quantity\": 2 }"
                            )
                    )
            )
    )
    @PostMapping("/{id}/apply")
    public Long apply(@PathVariable Long id,
                      @AuthenticationPrincipal CustomUserDetails me,
                      @RequestBody ApplyRequest req) {
        return service.apply(id, me.getId(), req);
    }

    @Operation(summary = "공동구매 신청 취소")
    @DeleteMapping("/{id}/apply")
    public void cancel(@PathVariable Long id,
                       @AuthenticationPrincipal CustomUserDetails me) {
        service.cancel(id, me.getId());
    }

    @Operation(summary = "입금 완료 처리")
    @PostMapping("/{id}/pay")
    public void pay(@PathVariable Long id,
                    @AuthenticationPrincipal CustomUserDetails me) {
        service.pay(id, me.getId());
    }

    @Operation(
            summary = "공동구매 수정",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "UpdateGroupPurchase",
                                    value = "{\n" +
                                            "  \"minParticipants\": 4,\n" +
                                            "  \"maxParticipants\": 12,\n" +
                                            "  \"applyDeadlineAt\": \"2025-08-24T12:00:00\",\n" +
                                            "  \"desiredDeliveryAt\": \"2025-08-28T10:00:00\",\n" +
                                            "  \"recipientName\": \"김철수\",\n" +
                                            "  \"recipientPhone\": \"010-9999-0000\",\n" +
                                            "  \"address\": \"서울특별시 마포구 합정동 1-2\",\n" +
                                            "  \"status\": \"PAYING\"\n" +
                                            "}"
                            )
                    )
            )
    )
    @PatchMapping("/{id}")
    public void update(@PathVariable Long id,
                       @AuthenticationPrincipal CustomUserDetails me,
                       @RequestBody GroupPurchaseUpdateRequest req) {
        service.update(me.getId(), id, req);
    }

    @Operation(summary = "공동구매 삭제")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal CustomUserDetails me) {
        service.delete(me.getId(), id);
    }
}
