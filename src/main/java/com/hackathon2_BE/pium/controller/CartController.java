package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.AddToCartRequest;
import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.CartItemResponse;
import com.hackathon2_BE.pium.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

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
}
