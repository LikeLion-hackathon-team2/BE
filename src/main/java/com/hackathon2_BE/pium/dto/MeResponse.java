package com.hackathon2_BE.pium.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon2_BE.pium.entity.DepositAccount;
import com.hackathon2_BE.pium.entity.GroupPurchase;
import com.hackathon2_BE.pium.entity.GroupPurchaseParticipant;
import com.hackathon2_BE.pium.entity.Order;
import com.hackathon2_BE.pium.entity.OrderItem;
import com.hackathon2_BE.pium.entity.Product;
import com.hackathon2_BE.pium.entity.ProductImage;
import com.hackathon2_BE.pium.entity.Shop;
import com.hackathon2_BE.pium.entity.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeResponse {

    // ===== 기본 인적 =====
    public Long id;
    public String username;
    public String role;
    public String phoneNumber;
    public String businessNumber;
    public String createdAt;

    // ===== 섹션 =====
    @JsonProperty("groupPurchases")
    public List<GroupPurchaseDto> groupPurchases;
    @JsonProperty("orders")
    public List<OrderDto> orders;
    @JsonProperty("shop")
    public ShopDto shop;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ShopDto {
    public Long id;
    public String name;

    @JsonProperty("businessNumber")
    public String ownerBusinessNumber;

    @JsonProperty("depositAccount")
    public DepositAccountDto depositAccount;

    public static ShopDto of(Shop s) {
        if (s == null) return null;
        ShopDto d = new ShopDto();
        d.id = s.getId();
        d.name = s.getName();
        User owner = s.getOwner();
        d.ownerBusinessNumber = (owner != null) ? owner.getBusinessNumber() : null;
        d.depositAccount = DepositAccountDto.from(s.getDepositAccount());
        return d;
    }
}

    // ---------- 팩토리 ----------
    public static MeResponse of(
            User user,
            List<GroupPurchaseParticipant> myParticipations,
            List<GroupPurchase> myOwned,
            List<Order> myOrders,
            Map<Long, Integer> participantCountByGpId // 공구ID -> 참여 인원
    ) {
        MeResponse m = new MeResponse();
        m.id = user.getId();
        m.username = user.getUsername();
        m.role = user.getRole() != null ? user.getRole().name() : null;
        m.phoneNumber = user.getPhoneNumber();
        m.businessNumber = user.getBusinessNumber();
        m.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().toString() : null;

        // 내가 '참여'한 공동구매
        List<GroupPurchaseDto> joined = myParticipations.stream()
                .map(p -> GroupPurchaseDto.fromParticipation(
                        p,
                        pickCount(participantCountByGpId, p.getGroupPurchase())
                ))
                .collect(Collectors.toList());

        // 내가 '개설'한 공동구매(중복 제거 후 병합)
        if (myOwned != null && !myOwned.isEmpty()) {
            Set<Long> exists = joined.stream().map(g -> g.groupPurchaseId).collect(Collectors.toSet());
            joined.addAll(
                myOwned.stream()
                       .filter(gp -> gp.getId() != null && !exists.contains(gp.getId()))
                       .map(gp -> GroupPurchaseDto.fromOwned(
                               gp,
                               pickCount(participantCountByGpId, gp)
                       ))
                       .collect(Collectors.toList())
            );
        }
        m.groupPurchases = joined;


        // 주문erDto::from).collect(Collectors.toList());
        m.orders = myOrders.stream().map(OrderDto::from).toList();
        m.shop = ShopDto.of(user.getShop());
        return m;
    }

    private static int pickCount(Map<Long, Integer> counts, GroupPurchase gp) {
        if (gp == null || gp.getId() == null) return 1;
        Integer v = counts.get(gp.getId());
        return Math.max(1, v != null ? v : 1);
    }

    // ====== 하위 DTO ======

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DepositAccountDto {
        public String bank;
        public String number;
        public String holder;
        public static DepositAccountDto from(DepositAccount da) {
            if (da == null) return null;
            DepositAccountDto d = new DepositAccountDto();
            d.bank = da.getBank();
            d.number = da.getNumber();
            d.holder = da.getHolder();
            return d;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GroupPurchaseDto {
        public Long groupPurchaseId;
        @JsonProperty("shop_name") public String shopName;
        public String status;
        public String role; // MEMBER | LEADER
        @JsonProperty("image_url") public String imageUrl;
        public Integer quantity;
        public Integer expectedPayment; // 필드가 없으므로 현재는 null
        public Integer shippingFee;     // 5000 또는 5000/n
        public DepositAccountDto depositAccount;
        public String joinedAt;

        // 참여자 관점
        public static GroupPurchaseDto fromParticipation(GroupPurchaseParticipant p, int participantCount) {
            GroupPurchase gp = p.getGroupPurchase();
            Product product = (gp != null) ? gp.getProduct() : null;
            Shop s = (product != null) ? product.getShop() : null;
            GroupPurchaseDto dto = new GroupPurchaseDto();
            dto.groupPurchaseId = gp != null ? gp.getId() : null;
            dto.shopName = s != null ? s.getName() : null;
            dto.status = (gp != null && gp.getStatus() != null) ? gp.getStatus().name() : null;
            dto.role = (p.getRole() != null) ? p.getRole().name() : "MEMBER";
            dto.imageUrl = ImagePickers.pickForGroupPurchase(gp);
            int qty = (p.getQuantity() != null) ? p.getQuantity() : 0;
            dto.quantity = qty;
            dto.expectedPayment = calcExpectedPayment(qty, product, participantCount);
            dto.shippingFee = ShippingPolicy.shippingFor(participantCount);
            dto.depositAccount = DepositAccountDto.from(s != null ? s.getDepositAccount() : null);
            dto.joinedAt = p.getJoinedAt() != null ? p.getJoinedAt().toString() : null;
            return dto;
        }

        // 개설자 관점
        public static GroupPurchaseDto fromOwned(GroupPurchase gp, int participantCount) {
            Product product = gp.getProduct();
            Shop s = (product != null) ? product.getShop() : null;

            GroupPurchaseDto dto = new GroupPurchaseDto();
            dto.groupPurchaseId = gp.getId();
            dto.shopName = (s != null) ? s.getName() : null;
            dto.status = (gp.getStatus() != null) ? gp.getStatus().name() : null;
            dto.role = "LEADER";
            dto.imageUrl = ImagePickers.pickForGroupPurchase(gp);
            dto.quantity = null;
            dto.expectedPayment = null;
            dto.shippingFee = ShippingPolicy.shippingFor(participantCount);
            dto.depositAccount = DepositAccountDto.from(s != null ? s.getDepositAccount() : null);
            dto.joinedAt = gp.getCreatedAt() != null ? gp.getCreatedAt().toString() : null;
            return dto;
        }

        private static Integer calcExpectedPayment(int qty, Product product, int participantCount) {
            if (product == null) return null;
            Integer unit = product.getPrice();
            if (unit == null) return null;

            int shipShare = ShippingPolicy.shippingFor(participantCount);

            long subtotal = (long) qty * unit;
            long total = subtotal + shipShare;

            if (total > Integer.MAX_VALUE) return Integer.MAX_VALUE;
            if (total < Integer.MIN_VALUE) return Integer.MIN_VALUE;
            return (int) total;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrderDto {
        public Long orderId;
        @JsonProperty("shop_name") public String shopName;
        public String status;
        @JsonProperty("image_url") public String imageUrl;
        @JsonProperty("grand_total") public Integer grandTotal;
        public Integer quantity;
        public String createdAt;

        public static OrderDto from(Order o) {
            OrderDto dto = new OrderDto();
            dto.orderId = o.getOrderId();
            dto.shopName = (o.getShop() != null) ? o.getShop().getName() : null;
            dto.status = (o.getOrderStatus() != null) ? o.getOrderStatus().name() : null;
            dto.grandTotal = o.getGrandTotal();
            dto.quantity = (o.getOrderItems() == null) ? null :
                    o.getOrderItems().stream().mapToInt(OrderItem::getQuantity).sum();
            dto.imageUrl = ImagePickers.pickForOrder(o);
            dto.createdAt = (o.getCreatedAt() != null) ? o.getCreatedAt().toString() : null;
            return dto;
        }
    }

    // ====== 정책/유틸 ======
    static class ShippingPolicy {
        private static final int BASE = 5000;
        static Integer shippingFor(int participantCount) {
            int n = Math.max(1, participantCount);
            return (int) Math.ceil(BASE / (double) n);
        }
    }

    static class ImagePickers {
        // 공구 카드: 상품 대표(ProductImage.isMain==true) → 첫 이미지 → (gp 자체 이미지는 참조 제거)
        static String pickForGroupPurchase(GroupPurchase gp) {
            if (gp == null) return null;
            Product product = gp.getProduct();
            if (product == null || product.getImages() == null || product.getImages().isEmpty())
                return null;

            // 대표 먼저
            Optional<String> main = product.getImages().stream()
                    .filter(ProductImage::isMain)
                    .map(ProductImage::getImageUrl)
                    .filter(Objects::nonNull)
                    .findFirst();
            if (main.isPresent()) return main.get();

            // 첫 이미지
            return product.getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        // 주문: 아이템들의 상품 대표 → 첫 이미지 → null
        static String pickForOrder(Order o) {
            if (o == null || o.getOrderItems() == null) return null;

            // 대표 먼저
            Optional<String> main = o.getOrderItems().stream()
                    .map(OrderItem::getProduct)
                    .filter(Objects::nonNull)
                    .map(Product::getImages)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .filter(com.hackathon2_BE.pium.entity.ProductImage::isMain)
                    .map(ProductImage::getImageUrl)
                    .filter(Objects::nonNull)
                    .findFirst();
            if (main.isPresent()) return main.get();

            // 첫 이미지
            return o.getOrderItems().stream()
                    .map(OrderItem::getProduct)
                    .filter(Objects::nonNull)
                    .map(Product::getImages)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .map(ProductImage::getImageUrl)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
    }
}
