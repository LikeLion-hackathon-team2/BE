package com.hackathon2_BE.pium.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hackathon2_BE.pium.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 검색/카테고리 조회 메서드
    Page<Product> findByNameContainingIgnoreCaseOrInfoContainingIgnoreCase(
            String nameKeyword,
            String infoKeyword,
            Pageable pageable
    );

    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

    @Query("""
        select p
        from Product p
        where p.userId = :userId
          and (:categoryId is null or p.category.id = :categoryId)
          and (
               :kw is null
               or lower(p.name) like lower(concat('%', :kw, '%'))
               or lower(p.info) like lower(concat('%', :kw, '%'))
          )
          and (
               :status is null
               or (:status = 'out_of_stock' and (p.stockQuantity is null or p.stockQuantity = 0))
               or (:status = 'active' and (p.stockQuantity is not null and p.stockQuantity > 0))
          )
        """)
    Page<Product> findSellerProducts(@Param("userId") Long userId,
                                     @Param("kw") String keyword,
                                     @Param("categoryId") Long categoryId,
                                     @Param("status") String status,
                                     Pageable pageable);
}
