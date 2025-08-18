package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.UploadProductImageResponse;
import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.CreateProductRequest;
import com.hackathon2_BE.pium.dto.ProductResponse;
import com.hackathon2_BE.pium.dto.SellerProductListResponse;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.exception.UnauthenticatedException;
import com.hackathon2_BE.pium.security.CustomUserDetails;
import com.hackathon2_BE.pium.service.ProductService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping
    public ResponseEntity<ApiResponse<SellerProductListResponse>> getSellerProducts(
                @RequestParam(name = "q", required = false) String q,
                @RequestParam(name = "category_id", required = false) Long categoryId,
                @RequestParam(name = "status", required = false) String status,   // active | out_of_stock
                @RequestParam(name = "sort", required = false) String sort,       // latest | price_asc | price_desc | stock_asc | stock_desc
                @RequestParam(name = "page", required = false) Integer page,      // default=1
                @RequestParam(name = "size", required = false) Integer size,      // default=20 (max 100)
                Authentication authentication
        ) {
            Long sellerId = extractUserId(authentication);

            SellerProductListResponse data =
                    productService.getSellerProducts(sellerId, q, categoryId, status, sort, page, size);

            ApiResponse<SellerProductListResponse> api =
                    new ApiResponse<>(true, "OK", "판매자 상품 목록 조회 성공", data);

            return ResponseEntity.ok(api);
        }

    @PostMapping(
            value = "/{productId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Transactional
    public ResponseEntity<ApiResponse<UploadProductImageResponse>> uploadProductImage(
            @PathVariable("productId") Long productId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "is_main", defaultValue = "false") boolean isMain,
            @RequestParam(name = "run_ai", defaultValue = "true") boolean runAi,
            Authentication authentication
    ) {
        Long sellerId = extractUserId(authentication);

        UploadProductImageResponse data =
                productService.uploadProductImage(sellerId, productId, file, isMain, runAi);

        ApiResponse<UploadProductImageResponse> api =
                new ApiResponse<>(true, "CREATED", "이미지가 업로드되었습니다.", data);

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
