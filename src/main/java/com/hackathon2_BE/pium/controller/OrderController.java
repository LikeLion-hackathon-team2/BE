package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.*;
import com.hackathon2_BE.pium.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses; // ← Swagger ApiResponses만 import
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders", description = "주문/결제 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "주문 미리보기",
            description = "장바구니 항목과 희망배송일을 바탕으로 금액/배송 가능일을 미리 계산합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( // ← FQN 사용
                    responseCode = "200",
                    description = "미리보기 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "preview-ok", value = """
                {
                  "success": true,
                  "code": "OK",
                  "message": "주문 미리보기",
                  "data": {
                    "items": [
                      {
                        "cart_item_id": 123,
                        "product_id": 101,
                        "name": "아삭한 사과 1kg",
                        "quantity": 2,
                        "unit_price": 12000,
                        "subtotal": 24000,
                        "image_url": "https://cdn.example.com/p/101.jpg",
                        "seller": { "user_id": 77, "shop_name": "그린농장" },
                        "spec": "부사/대과"
                      }
                    ],
                    "totals": { "products": 24000, "shipping": 0, "grand_total": 24000 },
                    "delivery": {
                      "requested_date": "2025-09-05",
                      "earliest_available": "2025-09-05",
                      "unavailable_dates": []
                    },
                    "payment_methods": [
                      { "key": "easypay_toss", "label": "토스페이" },
                      { "key": "easypay_naver", "label": "네이버페이" }
                    ]
                  }
                }
                """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = OrderPreviewRequest.class),
            examples = @ExampleObject(name = "preview-req", value = """
        {
          "cart_item_ids": [123, 124],
          "desired_delivery_date": "2025-09-05"
        }
        """)
    ))
    @PostMapping(value="/preview", consumes="application/json", produces="application/json")
    public ResponseEntity<ApiResponse<OrderPreviewResponse>> preview(
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody OrderPreviewRequest request
    ){
        var data = orderService.preview(userId, request);
        var body = new ApiResponse<>(true, "OK", "주문 미리보기", data);
        return ResponseEntity.ok(body);
    }

    @Operation(
            summary = "주문 생성 + 결제 준비",
            description = "미리보기 검증 후 주문을 생성하고, 결제 리다이렉트 정보를 돌려줍니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( // ← FQN 사용
                    responseCode = "201",
                    description = "주문 생성",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "create-ok", value = """
                {
                  "success": true,
                  "code": "CREATED",
                  "message": "주문이 생성되었습니다.",
                  "data": {
                    "order": {
                      "order_id": 555,
                      "status": "PENDING_PAYMENT",
                      "totals": { "products": 24000, "shipping": 0, "grand_total": 24000 }
                    },
                    "payment": {
                      "provider": "toss-test",
                      "method": "easypay_toss",
                      "payment_token": "tok_123",
                      "redirect_url": "https://pay.example.com/redirect?token=tok_123"
                    }
                  }
                }
                """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreateOrderRequest.class),
            examples = @ExampleObject(name = "create-req", value = """
        {
          "cart_item_ids": [123, 124],
          "shipping": {
            "receiver_name": "홍길동",
            "receiver_phone": "010-1111-2222",
            "address": "서울특별시 강남구 테헤란로 123"
          },
          "desired_delivery_date": "2025-09-05",
          "payment_method": "easypay_toss"
        }
        """)
    ))
    @PostMapping(consumes="application/json", produces="application/json")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> create(
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody CreateOrderRequest request,
            @Parameter(description = "멱등키(재시도 시 동일 응답 보장)", example = "a1b2c3d4-e5f6-7890")
            @RequestHeader(name = "Idempotency-Key", required = false) String idemKey
    ){
        var data = orderService.create(userId, request, idemKey);
        var body = new ApiResponse<>(true, "CREATED", "주문이 생성되었습니다.", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
