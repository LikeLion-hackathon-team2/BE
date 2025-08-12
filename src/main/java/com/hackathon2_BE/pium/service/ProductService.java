package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


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
}
