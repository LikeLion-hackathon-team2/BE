package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.*;
import com.hackathon2_BE.pium.service.CartQueryService;
import com.hackathon2_BE.pium.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CartQueryService cartQueryService;

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

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<Map<String, List<CartItemView>>>> getCartItems(
            @AuthenticationPrincipal(expression = "id") Long userId,
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

    @PatchMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Map<String, CartItemResponse>>> updateCartItem(
            @AuthenticationPrincipal(expression = "id") Long userId,
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

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeCartItem(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("id") Long cartItemId
    ) {
        cartService.removeCartItem(userId, cartItemId);

        var body = new ApiResponse<>(
                true,
                "OK",
                "장바구니 항목이 삭제되었습니다.",
                Map.<String, Object>of("cart_item_id", cartItemId) // 🔧 여기만 수정
        );
        return ResponseEntity.ok(body);
    }
}
