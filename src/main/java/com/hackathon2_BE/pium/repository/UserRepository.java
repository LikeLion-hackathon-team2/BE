package com.hackathon2_BE.pium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hackathon2_BE.pium.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByBusinessNumber(String businessNumber);
    java.util.Optional<User> findByUsername(String username);
}
