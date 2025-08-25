package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ProductImage i set i.isMain = false where i.product.id = :productId")
    void clearMainByProductId(@Param("productId") Long productId);

    Optional<ProductImage> findFirstByProduct_IdAndIsMainTrue(Long productId);
}
