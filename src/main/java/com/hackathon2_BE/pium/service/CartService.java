package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.AddToCartRequest;
import com.hackathon2_BE.pium.dto.CartItemResponse;
import com.hackathon2_BE.pium.entity.CartItem;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.repository.CartItemRepository;
import com.hackathon2_BE.pium.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartItemResponse addToCart(Long userId, AddToCartRequest req) {
        if (userId == null) {
            throw new org.springframework.security.core.AuthenticationException("UNAUTHORIZED") {};
        }

        if (req == null || req.productId() == null) {
            throw new InvalidInputException("product_id는 필수입니다.");
        }
        if (req.quantity() == null || req.quantity() < 1) {
            throw new InvalidInputException("수량은 1 이상의 정수여야 합니다.");
        }

        Product p = productRepository.findById(req.productId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품을 찾을 수 없습니다."));

        int unitPrice = Optional.ofNullable(p.getPrice()).orElse(0);
        int subtotal = unitPrice * req.quantity();

        CartItem ci = new CartItem();
        ci.setUserId(userId);
        ci.setProductId(p.getProductId());
        ci.setQuantity(req.quantity());
        ci.setUnitPrice(unitPrice);
        ci.setSubtotal(subtotal);
        ci.setCreatedAt(LocalDateTime.now());

        cartItemRepository.save(ci);

        return new CartItemResponse(
                ci.getCartItemId(),
                ci.getProductId(),
                ci.getQuantity(),
                ci.getUnitPrice(),
                ci.getSubtotal()
        );
    }
}
