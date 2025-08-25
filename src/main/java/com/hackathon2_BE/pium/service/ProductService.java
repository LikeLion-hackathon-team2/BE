package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.CreateProductRequest;
import com.hackathon2_BE.pium.dto.ProductOptionResponse;
import com.hackathon2_BE.pium.dto.ProductResponse;
import com.hackathon2_BE.pium.dto.SellerProductListResponse;
import com.hackathon2_BE.pium.dto.UploadProductImageResponse;
import com.hackathon2_BE.pium.entity.Category;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.entity.ProductImage;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.exception.ForbiddenException;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.exception.UnauthenticatedException;
import com.hackathon2_BE.pium.repository.CategoryRepository;
import com.hackathon2_BE.pium.repository.ProductImageRepository;
import com.hackathon2_BE.pium.repository.ProductRepository;
import com.hackathon2_BE.pium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    // ===== 공통 유틸 =====

    /** /uploads/... 형태의 경로를 절대 URL로 변환 */
    private String toAbsoluteUrl(String path) {
        if (path == null || path.isBlank()) return null;
        if (path.startsWith("http://") || path.startsWith("https://")) return path;
        String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return base + path;
    }

    /** 제품의 메인이미지 경로를 계산 (product.imageMainUrl 우선, 없으면 product_image 조회) */
    @Transactional(readOnly = true)
    protected String resolveMainImageAbsoluteUrl(Product p) {
        String path = p.getImageMainUrl();
        if (path == null || path.isBlank()) {
            path = productImageRepository.findFirstByProduct_IdAndIsMainTrue(p.getId())
                    .map(ProductImage::getImageUrl)
                    .orElse(null);
        }
        return toAbsoluteUrl(path);
    }

    // ===== 목록/검색 =====

    @Transactional(readOnly = true)
    public Page<Product> getProductList(String keyword, Long categoryId, Pageable pageable){
        if (keyword != null && !keyword.isBlank()){
            return productRepository.findByNameContainingIgnoreCaseOrInfoContainingIgnoreCase(keyword, keyword, pageable);
        } else if (categoryId != null){
            return productRepository.findByCategory_Id(categoryId, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    // ✅ 목록 DTO 매핑 시 이미지 URL 포함
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductListDto(String keyword, Long categoryId, Pageable pageable) {
        return getProductList(keyword, categoryId, pageable)
                .map(p -> ProductResponse.from(p, resolveMainImageAbsoluteUrl(p)));
    }

    // ===== 상세 =====

    @Transactional(readOnly = true)
    public Product getProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품을 찾을 수 없습니다."));
    }

    // ✅ 상세 DTO도 이미지 URL 포함
    @Transactional(readOnly = true)
    public ProductResponse getProductResponse(Long id) {
        Product p = getProductById(id);
        return ProductResponse.from(p, resolveMainImageAbsoluteUrl(p));
    }

    // ===== 옵션 =====

    @Transactional(readOnly = true)
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
                        .collect(Collectors.toList()))
                .orElse(List.of());

        int qMin = Optional.ofNullable(p.getQuantityMin()).orElse(1);
        int qStep = Optional.ofNullable(p.getQuantityStep()).orElse(1);
        int qMax = Optional.ofNullable(p.getQuantityMax()).orElse(stock);

        if (qMin < 1 || qMax < qMin || qStep < 1) {
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

    // ===== 등록 =====

    @Transactional
    public Product createBasicProduct(Long requesterUserId, CreateProductRequest req) {
        User seller = userRepository.findById(requesterUserId)
                .orElseThrow(() -> new UnauthenticatedException("인증이 필요합니다."));
        if (seller.getRole() == null || !seller.getRole().name().equals("SELLER")) {
            throw new ForbiddenException("판매자 권한이 필요합니다.");
        }

        Category category = null;
        if (req.getCategoryId() != null) {
            Long cid = req.getCategoryId();
            category = categoryRepository.findById(cid).orElseGet(() -> {
                String generatedName = "카테고리 " + cid;
                try {
                    categoryRepository.insertWithId(cid, generatedName);
                } catch (DataIntegrityViolationException e) {
                    // 동시성 등으로 이미 생성된 경우 무시
                }
                return categoryRepository.findById(cid)
                        .orElseThrow(() -> new ResourceNotFoundException("카테고리 생성/조회 실패: " + cid));
            });
        }

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

    @Transactional
    public UploadProductImageResponse uploadProductImage(Long sellerId,
                                                         Long productId,
                                                         MultipartFile file,
                                                         boolean isMain,
                                                         boolean runAi) {
        if (file == null || file.isEmpty()) {
            throw new InvalidInputException("파일이 비어있습니다.");
        }

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains(".")) ?
                original.substring(original.lastIndexOf('.') + 1).toLowerCase() : "";
        if (!ALLOWED_EXT.contains(ext)) {
            throw new InvalidInputException("지원하지 않는 파일 형식입니다.(jpg/png/webp)");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
        if (product.getUserId() == null || !product.getUserId().equals(sellerId)) {
            throw new ForbiddenException("해당 상품의 소유자가 아닙니다.");
        }

        try {
            Path dir = Paths.get(uploadRootDir, "products", String.valueOf(productId)).toAbsolutePath();
            Files.createDirectories(dir);

            String newName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            Path dest = dir.resolve(newName);
            file.transferTo(dest.toFile());

            String url = "/uploads/products/" + productId + "/" + newName;

            if (isMain) {
                productImageRepository.clearMainByProductId(productId);
                product.setImageMainUrl(url);
                productRepository.save(product);
            }

            ProductImage saved = productImageRepository.save(
                    ProductImage.builder()
                            .product(product)
                            .imageUrl(url)
                            .isMain(isMain)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            UploadProductImageResponse.Image imageDto = UploadProductImageResponse.Image.builder()
                    .image_id(saved.getId())
                    .image_url(saved.getImageUrl())
                    .is_main(saved.isMain())
                    .ai_processed(false)
                    .build();

            UploadProductImageResponse.ProductInfo productDto = UploadProductImageResponse.ProductInfo.builder()
                    .product_id(product.getId())
                    .grade_id(product.getGradeId())
                    .freshness(toUploadFreshness(product.getGradeId()))
                    .build();

            return UploadProductImageResponse.builder()
                    .image(imageDto)
                    .product(productDto)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    // ===== 판매자 목록 =====

    @Transactional(readOnly = true)
    public SellerProductListResponse getSellerProducts(Long sellerId,
                                                       String q,
                                                       Long categoryId,
                                                       String status,
                                                       String sort,
                                                       Integer page,
                                                       Integer size) {

        int p = (page == null || page < 1) ? 1 : page; // 1-based 입력
        int s = (size == null) ? 20 : size;
        if (s < 1 || s > 100) throw new InvalidInputException("size는 1~100 사이여야 합니다.");

        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            switch (status) {
                case "active", "out_of_stock" -> normalizedStatus = status;
                case "deleted" -> throw new InvalidInputException("deleted 상태 필터는 아직 지원되지 않습니다.");
                default -> throw new InvalidInputException("허용되지 않은 정렬 값입니다.");
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

        var items = pageResult.getContent().stream()
                .map(pv -> {
                    SellerProductListResponse.Freshness freshness = (pv.getGradeId() == null) ? null
                            : SellerProductListResponse.Freshness.builder()
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
                            .main_image_url(pv.getImageMainUrl()) // 필요하면 toAbsoluteUrl로 바꿔도 됨
                            .freshness(freshness)
                            .created_at(pv.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

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

    // ===== 신선도 =====

    private String toFreshnessLabel(Long gradeId) {
        if (gradeId == null) return null;
        return switch (gradeId.intValue()) {
            case 3 -> "매우 신선";
            case 2 -> "양호";
            case 1 -> "판매임박";
            default -> null;
        };
    }

    private UploadProductImageResponse.Freshness toUploadFreshness(Long gradeId) {
        if (gradeId == null) return null;
        String label = toFreshnessLabel(gradeId);
        if (label == null) return null;
        return UploadProductImageResponse.Freshness.builder()
                .grade(gradeId.intValue())
                .label(label)
                .build();
    }
}
