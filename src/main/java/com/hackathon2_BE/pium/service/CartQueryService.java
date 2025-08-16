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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CartItemView> getCartItems(Long userId, String idsParam) {
        // 1) 인증은 시큐리티가 처리, userId가 null이면 전역 핸들러에서 401 변환
        if (userId == null) {
            throw new org.springframework.security.core.AuthenticationException("UNAUTHORIZED") {};
        }

        // 2) ids 파싱/검증
        List<Long> ids = parseIds(idsParam);
        if (ids.isEmpty()) {
            throw new InvalidInputException("ids는 콤마로 구분된 정수 목록이어야 합니다.");
        }

        // 3) 본인 소유의 장바구니 항목만 조회
        List<CartItem> items = cartItemRepository.findByCartItemIdInAndUserId(ids, userId);
        if (items.isEmpty()) return List.of();

        // 4) product 일괄 조회 후 매핑
        Set<Long> productIds = items.stream().map(CartItem::getProductId).collect(Collectors.toSet());
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(p -> p.getId(), p -> p));

        // 5) DTO 변환
        return items.stream().map(ci -> {
            Product p = productMap.get(ci.getProductId());
            if (p == null) {
                throw new ResourceNotFoundException("상품을 찾을 수 없습니다. (product_id=" + ci.getProductId() + ")");
            }
            String spec = p.getInfo();
            CartItemView.Seller seller = new CartItemView.Seller(p.getUserId(), p.getShopName());

            return new CartItemView(
                    ci.getCartItemId(),
                    p.getId(),
                    p.getName(),
                    ci.getQuantity(),
                    ci.getUnitPrice(),         // 담을 당시 스냅샷 단가
                    p.getImageMainUrl(),
                    seller,
                    spec
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
