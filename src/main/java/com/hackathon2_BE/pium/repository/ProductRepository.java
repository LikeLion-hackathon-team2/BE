package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Long, Product> {
}
