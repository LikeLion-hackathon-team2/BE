package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.ProductOptionResponse;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 2-1) 상품 목록/검색/필터
    @GetMapping
    public ResponseEntity<Page<Product>> getProductList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductList(keyword, categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    // 2-2) 상품 상세 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Product>>> getProduct(@PathVariable Long id){
        Product product = productService.getProductById(id);

        Map<String, Product> data = Map.of("product", product);

        ApiResponse<Map<String, Product>> response = new ApiResponse<>(
                true,
                "OK",
                "상세 조회 성공",
                data
        );

        return ResponseEntity.ok(response);
    }

    // 3-1) 상품 구매 옵션 조히
    @GetMapping("/{id}/options")
    public ResponseEntity<ApiResponse<ProductOptionResponse>> getOptions(@PathVariable Long id) {
        ProductOptionResponse data = productService.getProductOptions(id);
        var body = new ApiResponse<>(true, "OK", "옵션 정보", data);
        return ResponseEntity.ok(body);
    }
}
