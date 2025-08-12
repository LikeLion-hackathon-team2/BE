package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.config.ApiResponse;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

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
}
