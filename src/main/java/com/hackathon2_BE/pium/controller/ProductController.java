package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.ProductOptionResponse;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses; // ← ApiResponses만 import
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Products", description = "상품 조회/상세/옵션 API")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "키워드/카테고리로 검색/필터하고 페이지네이션합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<Page<Product>> getProductList(
            @Parameter(description = "검색 키워드", example = "사과") @RequestParam(required = false) String keyword,
            @Parameter(description = "카테고리 ID", example = "1") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "페이지(0-base)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductList(keyword, categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "상품 상세 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( // ← FQN
                    responseCode = "200",
                    description = "상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "product-detail", value = """
                {
                  "success": true,
                  "code": "OK",
                  "message": "상세 조회 성공",
                  "data": {
                    "product": {
                      "id": 101,
                      "name": "아삭한 사과 1kg",
                      "price": 12000,
                      "stockQuantity": 57,
                      "info": "부사/대과",
                      "category": null,
                      "gradeId": 3,
                      "image_main_url": "https://cdn.example.com/p/101.jpg",
                      "shop_name": "그린농장",
                      "userId": 77
                    }
                  }
                }
                """))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Product>>> getProduct(
            @Parameter(description = "상품 ID", example = "101") @PathVariable Long id){
        Product product = productService.getProductById(id);
        Map<String, Product> data = Map.of("product", product);
        ApiResponse<Map<String, Product>> response =
                new ApiResponse<>(true, "OK", "상세 조회 성공", data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품 옵션 조회", description = "수량 한도, 단위 가격, 프리셋 등 구매 옵션을 제공합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( // ← FQN
                    responseCode = "200",
                    description = "옵션 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "options", value = """
                {
                  "success": true,
                  "code": "OK",
                  "message": "옵션 정보",
                  "data": {
                    "product_id": 101,
                    "unit_label": "봉",
                    "unit_price": 12000,
                    "stock_remaining": 57,
                    "presets": [1,2,3,5,10],
                    "quantity": { "min": 1, "max": 10, "step": 1 }
                  }
                }
                """))
            )
    })
    @GetMapping("/{id}/options")
    public ResponseEntity<ApiResponse<ProductOptionResponse>> getOptions(
            @Parameter(description = "상품 ID", example = "101") @PathVariable Long id) {
        ProductOptionResponse data = productService.getProductOptions(id);
        var body = new ApiResponse<>(true, "OK", "옵션 정보", data);
        return ResponseEntity.ok(body);
    }
}
