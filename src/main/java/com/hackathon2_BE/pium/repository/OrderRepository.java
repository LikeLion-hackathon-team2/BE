package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 내가 구매자로서 한 주문 전체 (아이템/상품/이미지까지 로딩)
    @Query("""
    select distinct o
      from Order o
      join fetch o.orderItems it
      join fetch it.product p
      join fetch p.shop s
     where o.consumer.id = :userId
     order by o.createdAt desc
""")
    List<Order> findDeepByBuyerId(Long userId);
}
