package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.*;
import com.hackathon2_BE.pium.service.CartQueryService;
import com.hackathon2_BE.pium.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Cart", description = "장바구니 API")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CartQueryService cartQueryService;

    @Operation(
            summary = "장바구니 담기",
            description = "상품을 장바구니에 추가합니다."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AddToCartRequest.class),
                    examples = @ExampleObject(name = "add-to-cart", value = """
                    {
                      "product_id": 101,
                      "quantity": 2
                    }
                    """)
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "담기 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "add-to-cart-res", value = """
                            {
                              "success": true,
                              "code": "CREATED",
                              "message": "장바구니에 담겼습니다.",
                              "data": {
                                "cart_item": {
                                  "cart_item_id": 12,
                                  "product_id": 101,
                                  "quantity": 2,
                                  "unit_price": 15000,
                                  "subtotal": 30000
                                }
                              }
                            }
                            """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, CartItemResponse>>> addToCart(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody AddToCartRequest request
    ){
        CartItemResponse item = cartService.addToCart(userId, request);

        var body = new ApiResponse<>(
                true,
                "CREATED",
                "장바구니에 담겼습니다.",
                Map.of("cart_item", item)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(
            summary = "장바구니 항목 조회",
            description = "ids 쿼리 파라미터(쉼표구분)로 특정 장바구니 항목들을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CartItemView.class)),
                            examples = @ExampleObject(name = "get-items-res", value = """
                            {
                              "success": true,
                              "code": "OK",
                              "message": "장바구니 항목 조회 성공",
                              "data": {
                                "items": [
                                  {
                                    "cart_item_id": 12,
                                    "product_id": 101,
                                    "name": "샤인머스캣 2kg",
                                    "quantity": 2,
                                    "unit_price": 15000,
                                    "image_url": "https://cdn.example.com/img/101-main.jpg",
                                    "seller": {
                                      "user_id": 77,
                                      "shop_name": "청춘농가"
                                    },
                                    "spec": "당도 17Brix"
                                  }
                                ]
                              }
                            }
                            """)
                    )
            )
    })
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<Map<String, List<CartItemView>>>> getCartItems(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Parameter(description = "조회할 cart_item_id 목록(쉼표구분)", example = "12,13,15")
            @RequestParam(name = "ids") String ids
    ) {
        List<CartItemView> views = cartQueryService.getCartItems(userId, ids);

        var body = new ApiResponse<>(
                true,
                "OK",
                "장바구니 항목 조회 성공",
                Map.of("items", views)
        );
        return ResponseEntity.ok(body);
    }

    @Operation(
            summary = "장바구니 수량 수정",
            description = "특정 장바구니 항목의 수량을 변경합니다."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UpdateCartItemRequest.class),
                    examples = @ExampleObject(name = "update-qty", value = """
                    { "quantity": 3 }
                    """)
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "update-res", value = """
                            {
                              "success": true,
                              "code": "OK",
                              "message": "장바구니 항목이 수정되었습니다.",
                              "data": {
                                "cart_item": {
                                  "cart_item_id": 12,
                                  "product_id": 101,
                                  "quantity": 3,
                                  "unit_price": 15000,
                                  "subtotal": 45000
                                }
                              }
                            }
                            """)
                    )
            )
    })
    @PatchMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Map<String, CartItemResponse>>> updateCartItem(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Parameter(description = "장바구니 항목 ID", example = "12")
            @PathVariable("id") Long cartItemId,
            @RequestBody UpdateCartItemRequest request
    ) {
        CartItemResponse updated = cartService.updateCartItem(userId, cartItemId, request);

        var body = new ApiResponse<>(
                true,
                "OK",
                "장바구니 항목이 수정되었습니다.",
                Map.of("cart_item", updated)
        );
        return ResponseEntity.ok(body);
    }

    @Operation(
            summary = "장바구니 항목 삭제",
            description = "특정 장바구니 항목을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "delete-res", value = """
                            {
                              "success": true,
                              "code": "OK",
                              "message": "장바구니 항목이 삭제되었습니다.",
                              "data": { "cart_item_id": 12 }
                            }
                            """)
                    )
            )
    })
    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeCartItem(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Parameter(description = "장바구니 항목 ID", example = "12")
            @PathVariable("id") Long cartItemId
    ) {
        cartService.removeCartItem(userId, cartItemId);

        var body = new ApiResponse<>(
                true,
                "OK",
                "장바구니 항목이 삭제되었습니다.",
                Map.<String, Object>of("cart_item_id", cartItemId)
        );
        return ResponseEntity.ok(body);
    }
}
