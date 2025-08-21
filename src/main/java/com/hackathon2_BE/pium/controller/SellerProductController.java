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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses; // ← ApiResponses만 import

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

@Tag(name = "Seller Products", description = "판매자 상품 관리 API")
@RestController
@RequestMapping("/api/seller/product")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;

    @Operation(summary = "상품 생성", description = "판매자가 기본 정보를 입력해 상품을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( // ← FQN
                    responseCode = "201",
                    description = "생성 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "create-product-ok", value = """
                {
                  "success": true,
                  "code": "CREATED",
                  "message": "상품이 생성되었습니다.",
                  "data": {
                    "product": {
                      "id": 201,
                      "name": "싱싱한 배 3kg",
                      "price": 29000,
                      "stockQuantity": 30,
                      "info": "신고배/대과",
                      "categoryId": 2,
                      "gradeId": 3,
                      "freshness": { "grade_id": 3, "grade": 3, "label": "매우 신선" },
                      "createdAt": "2025-08-22T00:00:00Z"
                    }
                  }
                }
                """))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreateProductRequest.class),
            examples = @ExampleObject(name = "create-product-req", value = """
        {
          "name": "싱싱한 배 3kg",
          "price": 29000,
          "stockQuantity": 30,
          "info": "신고배/대과",
          "categoryId": 2
        }
        """)
    ))
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @Valid @RequestBody CreateProductRequest req,
            @Parameter(hidden = true) Authentication authentication
    ) {
        Long userId = extractUserId(authentication);
        Product saved = productService.createBasicProduct(userId, req);
        ProductResponse body = ProductResponse.from(saved);
        ApiResponse<Map<String, Object>> api =
                new ApiResponse<>(true, "CREATED", "상품이 생성되었습니다.", Map.of("product", body));
        return ResponseEntity.status(HttpStatus.CREATED).body(api);
    }

    @Operation(summary = "판매자 상품 목록", description = "판매자의 상품을 조건별로 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SellerProductListResponse>> getSellerProducts(
            @Parameter(description = "검색어", example = "사과") @RequestParam(name = "q", required = false) String q,
            @Parameter(description = "카테고리 ID", example = "2") @RequestParam(name = "category_id", required = false) Long categoryId,
            @Parameter(description = "상태 필터(ex. ACTIVE, SOLD_OUT)", example = "ACTIVE") @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "정렬 키(ex. created,-price)", example = "created") @RequestParam(name = "sort", required = false) String sort,
            @Parameter(description = "페이지", example = "0") @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "사이즈", example = "20") @RequestParam(name = "size", required = false) Integer size,
            @Parameter(hidden = true) Authentication authentication
    ) {
        Long sellerId = extractUserId(authentication);
        SellerProductListResponse data =
                productService.getSellerProducts(sellerId, q, categoryId, status, sort, page, size);
        ApiResponse<SellerProductListResponse> api =
                new ApiResponse<>(true, "OK", "판매자 상품 목록 조회 성공", data);
        return ResponseEntity.ok(api);
    }

    @Operation(summary = "상품 이미지 업로드", description = "멀티파트로 상품 이미지를 업로드합니다. 메인 여부/AI 후처리 여부 지정 가능.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( // ← FQN
                    responseCode = "201",
                    description = "업로드 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "upload-ok", value = """
                {
                  "success": true,
                  "code": "CREATED",
                  "message": "이미지가 업로드되었습니다.",
                  "data": {
                    "image": {
                      "image_id": 9001,
                      "image_url": "https://cdn.example.com/p/201-main.jpg",
                      "is_main": true,
                      "ai_processed": true
                    },
                    "product": {
                      "product_id": 201,
                      "grade_id": 3,
                      "freshness": { "grade": 3, "label": "매우 신선" }
                    }
                  }
                }
                """))
            )
    })
    @PostMapping(
            value = "/{productId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Transactional
    public ResponseEntity<ApiResponse<UploadProductImageResponse>> uploadProductImage(
            @Parameter(description = "상품 ID", example = "201") @PathVariable("productId") Long productId,
            @Parameter(description = "업로드할 이미지 파일") @RequestPart("file") MultipartFile file,
            @Parameter(description = "메인 이미지로 지정 여부", example = "true") @RequestParam(name = "is_main", defaultValue = "false") boolean isMain,
            @Parameter(description = "AI 후처리 실행 여부", example = "true") @RequestParam(name = "run_ai", defaultValue = "true") boolean runAi,
            @Parameter(hidden = true) Authentication authentication
    ) {
        Long sellerId = extractUserId(authentication);
        UploadProductImageResponse data =
                productService.uploadProductImage(sellerId, productId, file, isMain, runAi);
        ApiResponse<UploadProductImageResponse> api =
                new ApiResponse<>(true, "CREATED", "이미지가 업로드되었습니다.", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(api);
    }

    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthenticatedException("인증이 필요합니다.");
        }
        var principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails cud && cud.getUser() != null && cud.getUser().getId() != null) {
            return cud.getUser().getId();
        }
        try { return Long.parseLong(auth.getName()); }
        catch (NumberFormatException e) { throw new IllegalStateException("인증 주체에서 userId를 추출할 수 없습니다."); }
    }
}
