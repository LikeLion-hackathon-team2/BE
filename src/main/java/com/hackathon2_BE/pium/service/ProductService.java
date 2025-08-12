package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.ProductOptionResponse;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 2-1) 상품 목록/검색/필터
    public Page<Product> getProductList(String keyword, Long categoryId, Pageable pageable){
        if(keyword != null && !keyword.isBlank()){
            return productRepository.findByNameContainingIgnoreCaseOrInfoContainingIgnoreCase(keyword, keyword, pageable);
        } else if(categoryId != null){
            return productRepository.findByCategoryId(categoryId, pageable);
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
                p.getProductId(),
                unitLabel,
                p.getPrice(),
                stock,
                presets,
                quantity
        );
    }
}
