package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.ProductOptionResponse;
import com.hackathon2_BE.pium.dto.ProductResponse;
import com.hackathon2_BE.pium.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Products", description = "상품 조회/상세/옵션 API")
@RestController
@RequestMapping(value = "/api/product", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /* ---------- 절대 URL 유틸 (중복 방지 강화) ---------- */
    private String toAbsoluteUrl(String pathOrUrl, HttpServletRequest req) {
        if (pathOrUrl == null || pathOrUrl.isBlank()) return null;

        // 절대 URL인 경우에도 "/uploads/"가 두 번 들어가면 한 번만 남기도록 정규화
        String lower = pathOrUrl.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            int first = pathOrUrl.indexOf("/uploads/");
            if (first >= 0) {
                int second = pathOrUrl.indexOf("/uploads/", first + "/uploads/".length());
                if (second > 0) {
                    // 첫 "/uploads/"부터 두 번째 "/uploads/" 직전까지만 유지 -> 중복 제거
                    return pathOrUrl.substring(0, second);
                }
            }
            return pathOrUrl;
        }

        // 상대 경로면 현재 요청 기준으로 절대화
        String scheme = req.getScheme();     // http/https
        String host   = req.getServerName(); // 43.201.84.186 등
        int port      = req.getServerPort(); // 8080 등

        String base = scheme + "://" + host + ((port == 80 || port == 443) ? "" : (":" + port));
        String rel  = pathOrUrl.startsWith("/") ? pathOrUrl : ("/" + pathOrUrl);

        String candidate = base + rel;

        // 혹시 base+rel 뒤에 rel이 한 번 더 붙은 형태 방지
        if (candidate.endsWith(rel + rel)) return base + rel;

        int firstRel = candidate.indexOf(rel);
        int secondRel = candidate.indexOf(rel, firstRel + rel.length());
        if (firstRel >= 0 && secondRel > 0) {
            return candidate.substring(0, secondRel);
        }
        return candidate;
    }

    @Operation(summary = "상품 목록 조회", description = "키워드/카테고리로 검색/필터하고 페이지네이션합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductList(
            @Parameter(description = "검색 키워드", example = "사과") @RequestParam(required = false) String keyword,
            @Parameter(description = "카테고리 ID", example = "1") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "페이지(0-base)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest req
    ) {
        Pageable pageable = PageRequest.of(page, size);

        // 서비스에서 Page<Product> 받아 imageUrl만 절대화하여 DTO 변환
        Page<ProductResponse> pageResult = productService.getProductList(keyword, categoryId, pageable)
                .map(p -> ProductResponse.from(p, toAbsoluteUrl(p.getImageMainUrl(), req)));

        Map<String, Object> data = Map.of(
                "items", pageResult.getContent(),
                "pagination", Map.of(
                        "page", pageResult.getNumber(),
                        "size", pageResult.getSize(),
                        "total", pageResult.getTotalElements(),
                        "totalPages", pageResult.getTotalPages()
                )
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "목록 조회 성공", data));
    }

    @Operation(summary = "상품 상세 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
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
                      "categoryId": 5,
                      "gradeId": 3,
                      "freshness": { "grade_id": 3, "grade": 3, "label": "매우 신선" },
                      "createdAt": "2025-08-22T00:00:00Z",
                      "imageUrl": "http://host:8080/uploads/products/101/xxx.jpg"
                    }
                  }
                }
                """))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, ProductResponse>>> getProduct(
            @Parameter(description = "상품 ID", example = "101") @PathVariable Long id,
            HttpServletRequest req
    ) {
        var p = productService.getProductById(id);
        String abs = toAbsoluteUrl(p.getImageMainUrl(), req);   // 한 번만 절대화
        ProductResponse pr = ProductResponse.from(p, abs);      // 추가 결합 금지

        Map<String, ProductResponse> data = Map.of("product", pr);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "상세 조회 성공", data));
    }

    @Operation(summary = "상품 옵션 조회", description = "수량 한도, 단위 가격, 프리셋 등 구매 옵션 제공")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "옵션 조회 성공"
            )
    })
    @GetMapping("/{id}/options")
    public ResponseEntity<ApiResponse<ProductOptionResponse>> getOptions(
            @Parameter(description = "상품 ID", example = "101") @PathVariable Long id
    ) {
        ProductOptionResponse data = productService.getProductOptions(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "옵션 정보", data));
    }
}
