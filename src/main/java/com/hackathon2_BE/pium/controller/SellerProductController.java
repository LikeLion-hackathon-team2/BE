package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.CreateProductRequest;
import com.hackathon2_BE.pium.dto.ProductResponse;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.exception.UnauthenticatedException;
import com.hackathon2_BE.pium.security.CustomUserDetails;
import com.hackathon2_BE.pium.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/seller/product")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @Valid @RequestBody CreateProductRequest req,
            Authentication authentication
    ) {
        Long userId = extractUserId(authentication);

        Product saved = productService.createBasicProduct(userId, req);
        ProductResponse body = ProductResponse.from(saved);

        ApiResponse<Map<String, Object>> api =
                new ApiResponse<>(true, "CREATED", "상품이 생성되었습니다.", Map.of("product", body));

        return ResponseEntity.status(HttpStatus.CREATED).body(api);
    }

    /**
     * Authentication → CustomUserDetails → userId 추출
     */
    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthenticatedException("인증이 필요합니다.");
        }

        var principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails cud) {
            var user = cud.getUser();
            if (user != null && user.getId() != null) {
                return user.getId();
            }
            try {
                return Long.parseLong(cud.getUsername());
            } catch (NumberFormatException ignore) {
                // fallback below
            }
        }

        try {
            return Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("인증 주체에서 userId를 추출할 수 없습니다.");
        }
    }
}
