package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCaseOrInfoContainingIgnoreCase(
            String nameKeyword,
            String infoKeyword,
            Pageable pageable
    );

    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
}
