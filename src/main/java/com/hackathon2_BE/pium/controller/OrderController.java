package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.*;
import com.hackathon2_BE.pium.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 미리보기
    @PostMapping(value="/preview", consumes="application/json", produces="application/json")
    public ResponseEntity<ApiResponse<OrderPreviewResponse>> preview(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody OrderPreviewRequest request
    ){
        var data = orderService.preview(userId, request);
        var body = new ApiResponse<>(true, "OK", "주문 미리보기", data);
        return ResponseEntity.ok(body);
    }

    // 주문 생성 + 결제 준비
    @PostMapping(consumes="application/json", produces="application/json")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> create(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody CreateOrderRequest request,
            @RequestHeader(name = "Idempotency-Key", required = false) String idemKey
    ){
        var data = orderService.create(userId, request, idemKey);
        var body = new ApiResponse<>(true, "CREATED", "주문이 생성되었습니다.", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
