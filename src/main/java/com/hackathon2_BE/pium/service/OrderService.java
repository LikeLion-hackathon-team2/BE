package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.*;
import com.hackathon2_BE.pium.entity.*;
import com.hackathon2_BE.pium.exception.ConflictException;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.payment.PaymentGateway;
import com.hackathon2_BE.pium.repository.CartItemRepository;
import com.hackathon2_BE.pium.repository.OrderRepository;
import com.hackathon2_BE.pium.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    private final PaymentGateway tossTestGateway;
    private final PaymentGateway naverMockGateway;

    @Transactional(readOnly = true)
    public OrderPreviewResponse preview(Long userId, OrderPreviewRequest req){
        if (userId == null) throw new org.springframework.security.core.AuthenticationException("UNAUTHORIZED") {};

        var ids = Optional.ofNullable(req.cartItemIds()).orElse(List.of());
        if (ids.isEmpty()) throw new InvalidInputException("cart_item_ids는 비어 있을 수 없습니다.");

        // 장바구니 항목 로드(본인 것만)
        var items = cartItemRepository.findByCartItemIdInAndUserId(ids, userId);
        if (items.isEmpty()) return new OrderPreviewResponse(List.of(),
                new OrderPreviewTotals(0,0,0),
                new OrderPreviewDelivery(req.desiredDeliveryDate(), req.desiredDeliveryDate(), List.of()),
                supportedMethods());

        // 상품 일괄 조회
        var pids = items.stream().map(CartItem::getProductId).collect(Collectors.toSet());
        var productMap = productRepository.findAllById(pids).stream()
                .collect(Collectors.toMap(Product::getProductId, p->p));

        List<ApiFieldError> conflicts = new ArrayList<>();
        List<OrderPreviewItem> out = new ArrayList<>();
        int totalProducts = 0;

        for (var ci : items) {
            var p = productMap.get(ci.getProductId());
            if (p == null) throw new ResourceNotFoundException("상품을 찾을 수 없습니다. (product_id="+ci.getProductId()+")");

            int currentPrice = Optional.ofNullable(p.getPrice()).orElse(0);
            int stock = Optional.ofNullable(p.getStockQuantity()).orElse(0);

            if (ci.getQuantity() > stock) {
                conflicts.add(ApiFieldError.of("items[0].quantity", "요청 " + ci.getQuantity() + ", 남은 수량 " + stock));
            }
            if (!Objects.equals(ci.getUnitPrice(), currentPrice)) {
                conflicts.add(ApiFieldError.of("items[0].unit_price", "가격이 " + ci.getUnitPrice() + "→" + currentPrice + "으로 변경되었습니다."));
            }

            int subtotal = ci.getQuantity() * ci.getUnitPrice();
            totalProducts += subtotal;

            var seller = new OrderPreviewItem.Seller(p.getUserId(), p.getShopName());
            out.add(new OrderPreviewItem(
                    ci.getCartItemId(), p.getProductId(), p.getName(),
                    ci.getQuantity(), ci.getUnitPrice(), subtotal,
                    p.getImageMainUrl(), seller, p.getInfo()
            ));
        }

        if (!conflicts.isEmpty()) {
            throw new ConflictException("항목 중 재고 또는 금액이 변경되었습니다.", conflicts);
        }

        var totals = new OrderPreviewTotals(totalProducts, 0, totalProducts);
        var delivery = new OrderPreviewDelivery(req.desiredDeliveryDate(), req.desiredDeliveryDate(), List.of());
        return new OrderPreviewResponse(out, totals, delivery, supportedMethods());
    }

    private List<OrderPreviewResponse.SimpleMethod> supportedMethods() {
        return List.of(
                new OrderPreviewResponse.SimpleMethod("easypay_toss", "토스페이"),
                new OrderPreviewResponse.SimpleMethod("easypay_naver", "네이버페이")
        );
    }

    @Transactional
    public CreateOrderResponse create(Long userId, CreateOrderRequest req, String idemKey){
        if (userId == null) throw new org.springframework.security.core.AuthenticationException("UNAUTHORIZED") {};
        if (req.cartItemIds()==null || req.cartItemIds().isEmpty()) throw new InvalidInputException("cart_item_ids는 필수입니다.");
        if (req.shipping()==null) throw new InvalidInputException("shipping은 필수입니다.");
        if (req.paymentMethod()==null || req.paymentMethod().isBlank()) throw new InvalidInputException("payment_method는 필수입니다.");

        var preview = preview(userId, new OrderPreviewRequest(req.cartItemIds(), req.desiredDeliveryDate()));

        var order = new Order();
        order.setUserId(userId);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        order.setDeliveryDate(req.desiredDeliveryDate());
        order.setDeliveryAddress(req.shipping().address());
        order.setReceiverName(req.shipping().receiverName());
        order.setReceiverPhone(req.shipping().receiverPhone());
        order.setTotalProductsPrice(preview.totals().products());
        order.setShippingFee(preview.totals().shipping());
        order.setGrandTotal(preview.totals().grandTotal());
        order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        var cartItems = cartItemRepository.findByCartItemIdInAndUserId(req.cartItemIds(), userId);
        for (var ci : cartItems) {
            var oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductId(ci.getProductId());
            oi.setQuantity(ci.getQuantity());
            oi.setTotalPrice(ci.getQuantity() * ci.getUnitPrice());
            order.getOrderItems().add(oi);
        }

        PaymentGateway gw = switch (req.paymentMethod()) {
            case "easypay_toss"  -> tossTestGateway;
            case "easypay_naver" -> naverMockGateway;
            default -> throw new InvalidInputException("지원하지 않는 결제수단입니다: " + req.paymentMethod());
        };

        var init = gw.init(
                "order-" + order.getOrderId(),
                preview.totals().grandTotal(),
                req.paymentMethod(),
                "http://localhost:8080/payments/success",  // 리다이렉트 URL (임시)
                "http://localhost:8080/payments/fail",
                idemKey
        );

        var orderPart = new CreateOrderResponse.OrderPart(order.getOrderId(), order.getOrderStatus().name(),
                new OrderPreviewTotals(order.getTotalProductsPrice(), order.getShippingFee(), order.getGrandTotal()));
        var payPart = new CreateOrderResponse.PaymentPart(init.provider(), init.method(), init.paymentToken(), init.redirectUrl());
        return new CreateOrderResponse(orderPart, payPart);
    }
}
