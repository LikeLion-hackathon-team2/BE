package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.CartItemView;
import com.hackathon2_BE.pium.entity.CartItem;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.repository.CartItemRepository;
import com.hackathon2_BE.pium.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /** 선택된 ids만 조회 */
    @Transactional(readOnly = true)
    public List<CartItemView> getCartItems(Long userId, String idsParam) {
        if (userId == null) {
            throw new org.springframework.security.core.AuthenticationException("UNAUTHORIZED") {};
        }

        List<Long> ids = parseIds(idsParam);
        if (ids.isEmpty()) {
            throw new InvalidInputException("ids는 콤마로 구분된 정수 목록이어야 합니다.");
        }

        List<CartItem> items = cartItemRepository.findByCartItemIdInAndUserId(ids, userId);
        if (items.isEmpty()) return List.of();

        return toViews(items);
    }

    /** 전체 장바구니 조회 */
    @Transactional(readOnly = true)
    public List<CartItemView> getAllCartItems(Long userId) {
        if (userId == null) {
            throw new org.springframework.security.core.AuthenticationException("UNAUTHORIZED") {};
        }
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        if (items.isEmpty()) return List.of();
        return toViews(items);
    }

    private List<CartItemView> toViews(List<CartItem> items) {
        Set<Long> productIds = items.stream().map(CartItem::getProductId).collect(Collectors.toSet());
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        return items.stream().map(ci -> {
            Product p = productMap.get(ci.getProductId());
            if (p == null) {
                throw new ResourceNotFoundException("상품을 찾을 수 없습니다. (product_id=" + ci.getProductId() + ")");
            }
            return new CartItemView(
                    ci.getCartItemId(),
                    p.getId(),
                    p.getName(),
                    ci.getQuantity(),
                    ci.getUnitPrice(),        // 담을 당시 스냅샷 단가
                    p.getImageMainUrl(),
                    new CartItemView.Seller(p.getUserId(), p.getShopName()),
                    p.getInfo()
            );
        }).toList();
    }

    private List<Long> parseIds(String idsParam) {
        if (idsParam == null || idsParam.isBlank()) return List.of();
        try {
            return Arrays.stream(idsParam.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .toList();
        } catch (NumberFormatException e) {
            throw new InvalidInputException("ids는 콤마로 구분된 정수 목록이어야 합니다.");
        }
    }
}
