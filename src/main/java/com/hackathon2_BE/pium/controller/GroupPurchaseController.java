package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.*;
import com.hackathon2_BE.pium.entity.GroupPurchaseStatus;
import com.hackathon2_BE.pium.security.CustomUserDetails;
import com.hackathon2_BE.pium.service.GroupPurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name="create-ok", value = "123")))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GroupPurchaseCreateRequest.class),
            examples = @ExampleObject(name="create-req", value = """
        {
          "productId": 101,
          "leaderQuantity": 1,
          "minParticipants": 3,
          "maxParticipants": 10,
          "applyDeadlineAt": "2025-09-02T23:59:00",
          "desiredDeliveryAt": "2025-09-06T10:00:00",
          "recipientName": "홍길동",
          "recipientPhone": "010-1234-5678",
          "address": "서울특별시 강남구 테헤란로 123"
        }
        """)))
    @PostMapping
    public Long create(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody GroupPurchaseCreateRequest req
    ) {
        return service.create(me.getId(), req);
    }

    @Operation(summary = "공동구매 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public Page<GroupPurchaseListResponse> list(
            @Parameter(description = "상태 필터", example = "RECRUITING") @RequestParam(required = false) GroupPurchaseStatus status,
            Pageable pageable
    ) {
        return service.list(status, pageable);
    }

    @Operation(summary = "공동구매 상세 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{id}")
    public GroupPurchaseDetailResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "공동구매 상세(뷰) 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{id}/view")
    public GroupPurchaseDetailViewResponse getView(@PathVariable Long id) {
        return service.getView(id);
    }

    @Operation(summary = "공동구매 참여 신청")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신청 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name="apply-ok", value = "9876")))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ApplyRequest.class),
            examples = @ExampleObject(name="apply-req", value = """
        { "quantity": 2 }
        """)))
    @PostMapping("/{id}/apply")
    public Long apply(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody ApplyRequest req
    ) {
        return service.apply(id, me.getId(), req);
    }

    @Operation(summary = "공동구매 참여 취소")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "취소 성공")
    })
    @DeleteMapping("/{id}/apply")
    public void cancel(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me
    ) {
        service.cancel(id, me.getId());
    }

    @Operation(summary = "입금 완료 처리")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "처리 성공")
    })
    @PostMapping("/{id}/pay")
    public void pay(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me
    ) {
        service.pay(id, me.getId());
    }

    @Operation(summary = "공동구매 수정")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GroupPurchaseUpdateRequest.class),
            examples = @ExampleObject(name="update-req", value = """
        {
          "minParticipants": 4,
          "maxParticipants": 12,
          "applyDeadlineAt": "2025-09-03T23:59:00",
          "desiredDeliveryAt": "2025-09-07T10:00:00",
          "recipientName": "홍길동",
          "recipientPhone": "010-5678-1234",
          "address": "서울특별시 송파구 중대로 45, 1203호",
          "status": "PAYING"
        }
        """)))
    @PatchMapping("/{id}")
    public void update(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody GroupPurchaseUpdateRequest req
    ) {
        service.update(me.getId(), id, req);
    }

    @Operation(summary = "공동구매 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me
    ) {
        service.delete(me.getId(), id);
    }
}
