package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public Product getProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품을 찾을 수 없습니다."));
    }
}
