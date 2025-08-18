package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 명시한 category_id로 직접 INSERT (MySQL 네이티브)
    @Modifying
    @Query(value = "INSERT INTO category (category_id, name) VALUES (:id, :name)", nativeQuery = true)
    void insertWithId(@Param("id") Long id, @Param("name") String name);
}
