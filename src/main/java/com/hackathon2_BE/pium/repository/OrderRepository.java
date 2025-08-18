package com.hackathon2_BE.pium.repository;

import com.hackathon2_BE.pium.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
