package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.CreateProductRequest;
import com.hackathon2_BE.pium.dto.ProductOptionResponse;
import com.hackathon2_BE.pium.entity.Category;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.exception.ForbiddenException;
import com.hackathon2_BE.pium.exception.UnauthenticatedException;
import com.hackathon2_BE.pium.repository.CategoryRepository;
import com.hackathon2_BE.pium.repository.ProductRepository;
import com.hackathon2_BE.pium.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

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
                // name은 NOT NULL일 수 있으므로 기본 이름 만들어줌
                String generatedName = "카테고리 " + cid;
                try {
                    categoryRepository.insertWithId(cid, generatedName);
                } catch (DataIntegrityViolationException e) {
                    // 동시성 등으로 이미 누가 넣었다면 무시하고 조회로 이어감
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
                .category(category)               // FK 연결 (null 허용)
                .userId(seller.getId())
                .gradeId(null)                    // 이미지 분석 전
                .createdAt(LocalDateTime.now())   // 또는 @PrePersist
                .build();

        return productRepository.save(p);
    }
  
}
