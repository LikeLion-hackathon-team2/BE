package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.UploadProductImageResponse;
import com.hackathon2_BE.pium.dto.CreateProductRequest;
import com.hackathon2_BE.pium.dto.ProductOptionResponse;
import com.hackathon2_BE.pium.dto.SellerProductListResponse;
import com.hackathon2_BE.pium.entity.ProductImage;
import com.hackathon2_BE.pium.entity.Category;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.exception.ForbiddenException;
import com.hackathon2_BE.pium.exception.UnauthenticatedException;
import com.hackathon2_BE.pium.repository.ProductImageRepository;
import com.hackathon2_BE.pium.repository.CategoryRepository;
import com.hackathon2_BE.pium.repository.ProductRepository;
import com.hackathon2_BE.pium.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductImageRepository productImageRepository;
    @Value("${app.upload.dir:uploads}")
    private String uploadRootDir;
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    // 2-1) 상품 목록/검색/필터
    public Page<Product> getProductList(String keyword, Long categoryId, Pageable pageable){
        if(keyword != null && !keyword.isBlank()){
            return productRepository.findByNameContainingIgnoreCaseOrInfoContainingIgnoreCase(keyword, keyword, pageable);
        } else if(categoryId != null){
            return productRepository.findByCategory_Id(categoryId, pageable);
        } else{
            return productRepository.findAll(pageable);
        }
    }

    // 2-2) 상품 상세 정보 조회
    public Product getProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품을 찾을 수 없습니다."));
    }

    // 3-1) 상품 구매 옵션 조회
    public ProductOptionResponse getProductOptions(Long productId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품을 찾을 수 없습니다."));

        String unitLabel = (p.getUnitLabel() == null || p.getUnitLabel().isBlank()) ? "개" : p.getUnitLabel();
        int stock = p.getStockQuantity() == null ? 0 : p.getStockQuantity();

        List<Integer> presets = Optional.ofNullable(p.getPresetsCsv())
                .filter(s -> !s.isBlank())
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(x -> !x.isEmpty())
                        .map(Integer::valueOf)
                        .toList())
                .orElse(List.of());

        int qMin = Optional.ofNullable(p.getQuantityMin()).orElse(1);
        int qStep = Optional.ofNullable(p.getQuantityStep()).orElse(1);
        int qMax = Optional.ofNullable(p.getQuantityMax()).orElse(stock);

        if (qMin < 1 || qMax < qMin || qStep < 1) {
            // 전역 예외 처리기에서 INVALID_INPUT으로 변환되도록
            throw new InvalidInputException("요청 필드가 올바르지 않습니다.");
        }

        var quantity = new ProductOptionResponse.Quantity(qMin, qMax, qStep);

        return new ProductOptionResponse(
                p.getId(),
                unitLabel,
                p.getPrice(),
                stock,
                presets,
                quantity
        );
    }

    // 6-1) 상품 등록(1)-상품 기본정보 생성
    @Transactional
    public Product createBasicProduct(Long requesterUserId, CreateProductRequest req) {
        // 1) 인증/권한
        User seller = userRepository.findById(requesterUserId)
                .orElseThrow(() -> new UnauthenticatedException("인증이 필요합니다."));
        if (seller.getRole() == null || !seller.getRole().name().equals("SELLER")) {
            throw new ForbiddenException("판매자 권한이 필요합니다.");
        }

        // 2) 카테고리: 숫자 id만 받음. 있으면 사용, 없으면 그 id로 새로 INSERT
        Category category = null;
        if (req.getCategoryId() != null) {
            Long cid = req.getCategoryId();

            category = categoryRepository.findById(cid).orElseGet(() -> {
                String generatedName = "카테고리 " + cid;
                try {
                    categoryRepository.insertWithId(cid, generatedName);
                } catch (DataIntegrityViolationException e) {
                }
                return categoryRepository.findById(cid)
                        .orElseThrow(() -> new ResourceNotFoundException("카테고리 생성/조회 실패: " + cid));
            });
        }

        // 3) 상품 저장
        Product p = Product.builder()
                .name(req.getName())
                .price(req.getPrice())
                .stockQuantity(req.getStockQuantity())
                .info(req.getInfo())
                .category(category)
                .userId(seller.getId())
                .gradeId(null)
                .createdAt(LocalDateTime.now())
                .build();

        return productRepository.save(p);
    }
    
    // 6-2) 상품 등록(2)-상품 이미지 업로드
    @Transactional
    public UploadProductImageResponse uploadProductImage(Long sellerId,
                                                        Long productId,
                                                        MultipartFile file,
                                                        boolean isMain,
                                                        boolean runAi) {
        if (file == null || file.isEmpty()) {
            throw new InvalidInputException("파일이 비어있습니다.");
        }

        // 파일 검증 (확장자)
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains(".")) ?
                original.substring(original.lastIndexOf('.') + 1).toLowerCase() : "";
        if (!ALLOWED_EXT.contains(ext)) {
            throw new InvalidInputException("지원하지 않는 파일 형식입니다.(jpg/png/webp)");
        }

        // 상품 조회 + 소유자 검증
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
        if (product.getUserId() == null || !product.getUserId().equals(sellerId)) {
            throw new ForbiddenException("해당 상품의 소유자가 아닙니다.");
        }

        // 저장 경로 준비
        try {
            Path dir = Paths.get(uploadRootDir, "products", String.valueOf(productId)).toAbsolutePath();
            Files.createDirectories(dir);

            String newName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            Path dest = dir.resolve(newName);

            // 저장
            file.transferTo(dest.toFile());

            // 정적 접근 URL 구성 (/uploads/** 로 매핑)
            String url = "/uploads/products/" + productId + "/" + newName;

            // 대표 이미지 플래그 정리
            if (isMain) {
                productImageRepository.clearMainByProductId(productId);
                product.setImageMainUrl(url); // Product 엔티티에 imageMainUrl 필드가 이미 있음
                productRepository.save(product);
            }

            // 엔티티 저장
            ProductImage saved = productImageRepository.save(
                    ProductImage.builder()
                            .product(product)
                            .imageUrl(url)
                            .isMain(isMain)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            // 응답 DTO 구성 (AI는 미적용)
            UploadProductImageResponse.Image imageDto = UploadProductImageResponse.Image.builder()
                    .image_id(saved.getId())
                    .image_url(saved.getImageUrl())
                    .is_main(saved.isMain())
                    .ai_processed(false)
                    .build();

            UploadProductImageResponse.ProductInfo productDto = UploadProductImageResponse.ProductInfo.builder()
                    .product_id(product.getId())
                    .grade_id(product.getGradeId())
                    .build();

            return UploadProductImageResponse.builder()
                    .image(imageDto)
                    .product(productDto)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    // 6-3) 판매자 상품 목록 조회
    @Transactional(readOnly = true)
    public SellerProductListResponse getSellerProducts(Long sellerId,
                                                       String q,
                                                       Long categoryId,
                                                       String status,
                                                       String sort,
                                                       Integer page,
                                                       Integer size) {

        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null) ? 20 : size;
        if (s < 1 || s > 100) {
            throw new InvalidInputException("size는 1~100 사이여야 합니다.");
        }

        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            switch (status) {
                case "active", "out_of_stock" -> normalizedStatus = status;
                case "deleted" -> throw new InvalidInputException("deleted 상태 필터는 아직 지원되지 않습니다.");
                default -> throw new InvalidInputException("허용되지 않은 상태 값입니다.");
            }
        }

        Sort sortObj = switch (sort == null ? "latest" : sort) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "stock_asc" -> Sort.by(Sort.Direction.ASC, "stockQuantity");
            case "stock_desc" -> Sort.by(Sort.Direction.DESC, "stockQuantity");
            default -> throw new InvalidInputException("허용되지 않은 정렬 값입니다.");
        };

        PageRequest pageable = PageRequest.of(p - 1, s, sortObj);

        var pageResult = productRepository.findSellerProducts(
                sellerId,
                (q == null || q.isBlank()) ? null : q,
                categoryId,
                normalizedStatus,
                pageable
        );

        var items = pageResult.stream().map(pv -> {
            var freshness = (pv.getGradeId() == null) ? null :
                    SellerProductListResponse.Freshness.builder()
                            .grade_id(pv.getGradeId())
                            .grade(pv.getGradeId().intValue())
                            .label(toFreshnessLabel(pv.getGradeId()))
                            .build();

            String computedStatus = (pv.getStockQuantity() == null || pv.getStockQuantity() == 0)
                    ? "out_of_stock" : "active";

            return SellerProductListResponse.Item.builder()
                    .product_id(pv.getId())
                    .name(pv.getName())
                    .price(pv.getPrice())
                    .stock_quantity(pv.getStockQuantity())
                    .status(computedStatus)
                    .category_id(pv.getCategory() != null ? pv.getCategory().getId() : null)
                    .main_image_url(pv.getImageMainUrl())
                    .freshness(freshness)
                    .created_at(pv.getCreatedAt())
                    .updated_at(pv.getUpdatedAt())
                    .build();
        }).toList();

        var pagination = SellerProductListResponse.Pagination.builder()
                .page(p)
                .size(s)
                .total(pageResult.getTotalElements())
                .build();

        return SellerProductListResponse.builder()
                .items(items)
                .pagination(pagination)
                .build();
    }

    // === 신선도 라벨: 3단계(3=매우 신선, 2=양호, 1=판매임박) ===
    private String toFreshnessLabel(Long gradeId) {
        if (gradeId == null) return null;
        return switch (gradeId.intValue()) {
            case 3 -> "매우 신선";
            case 2 -> "양호";
            case 1 -> "판매임박";
            default -> null;
        };
    }
}

