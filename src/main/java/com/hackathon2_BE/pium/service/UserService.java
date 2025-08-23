package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.MeResponse;
import com.hackathon2_BE.pium.dto.UserDTO;
import com.hackathon2_BE.pium.entity.GroupPurchase;
import com.hackathon2_BE.pium.entity.GroupPurchaseParticipant;
import com.hackathon2_BE.pium.entity.Order;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.entity.Shop;
import com.hackathon2_BE.pium.entity.DepositAccount;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.UnauthenticatedException;
import com.hackathon2_BE.pium.exception.UsernameAlreadyExistsException;
import com.hackathon2_BE.pium.repository.GroupPurchaseParticipantRepository;
import com.hackathon2_BE.pium.repository.GroupPurchaseRepository;
import com.hackathon2_BE.pium.repository.OrderRepository;
import com.hackathon2_BE.pium.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupPurchaseParticipantRepository gppRepository;
    private final GroupPurchaseRepository gpRepository;
    private final OrderRepository orderRepository;

    // ===================== 회원가입 =====================
    @Transactional
    public User signup(UserDTO userDTO) {

        if (!isValidUsername(userDTO.getUsername()))
            throw new InvalidInputException("아이디는 4~20자의 영문 소문자, 숫자, 밑줄만 허용됩니다.");
        if (!isValidPassword(userDTO.getPassword()))
            throw new InvalidInputException("비밀번호는 8~64자이며 영문과 숫자를 포함해야 합니다.");
        if (!isValidRole(userDTO.getRole()))
            throw new InvalidInputException("role은 'consumer' 또는 'seller'만 허용됩니다.");

        if (userRepository.existsByUsername(userDTO.getUsername()))
            throw new UsernameAlreadyExistsException("이미 사용 중인 아이디입니다.");

        String normalizedPhone = normalizePhone(userDTO.getPhoneNumber());
        if (!isValidPhone(normalizedPhone))
            throw new InvalidInputException("전화번호는 숫자 10~11자리여야 합니다.");

        boolean isSeller = "seller".equalsIgnoreCase(userDTO.getRole());

        // ── 사업자번호(판매자만─
        String normalizedBusinessNumber = null;
        if (isSeller) {
            normalizedBusinessNumber = normalizeBusinessNumber(userDTO.getBusinessNumber());
            if (!isValidBusinessNumber(normalizedBusinessNumber))
                throw new InvalidInputException("사업자 번호는 숫자 10자리여야 합니다.");
            if (userRepository.existsByBusinessNumber(normalizedBusinessNumber))
                throw new InvalidInputException("이미 등록된 사업자 번호입니다.");
        }

        // ── Shop/Account 생성(판매자만) ──
        Shop shop = null;
        if (isSeller) {
            String shopName = normalizeShopName(userDTO.getShopName());
            if (!isValidShopName(shopName))
                throw new InvalidInputException("가게명은 1~100자여야 하며 앞뒤 공백만으로 구성될 수 없습니다.");

            shop = new Shop();
            shop.setName(shopName);

            if (userDTO.getDepositAccount() != null) {
                var accDto = userDTO.getDepositAccount();

                String bank   = nullToEmpty(accDto.getBank()).trim();
                String number = normalizeAccountNumber(accDto.getNumber());
                String holder = nullToEmpty(accDto.getHolder()).trim();

                if (!isValidBankOrHolder(bank))
                    throw new InvalidInputException("은행명은 1~100자의 한글/영문/숫자/(),-_. 만 허용됩니다.");
                if (!isValidAccountNumber(number))
                    throw new InvalidInputException("계좌번호는 숫자 6~20자리여야 합니다.");
                if (!isValidBankOrHolder(holder))
                    throw new InvalidInputException("예금주명은 1~100자의 한글/영문/숫자/(),-_. 만 허용됩니다.");

                DepositAccount account = new DepositAccount();
                account.setBank(bank);
                account.setNumber(number);
                account.setHolder(holder);

                shop.setDepositAccount(account);
            }
        }

        // User 생성
        User.Role roleEnum = isSeller ? User.Role.SELLER : User.Role.CONSUMER;
        String hashedPw = passwordEncoder.encode(userDTO.getPassword());

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(hashedPw)
                .role(roleEnum)
                .phoneNumber(normalizedPhone)
                .businessNumber(isSeller ? normalizedBusinessNumber : null)
                .shop(shop)
                .build();

        // 양방향 고정: shop.owner 설정 (편의 메서드가 없다면 직접 연결)
        if (shop != null) {
            shop.setOwner(user);
        }

        // cascade 설정에 따라 shop/account 함께 저장
        return userRepository.save(user);
    }

    // ===================== 내 정보 조회 (Auth → ID 추출 래퍼) =====================
    @Transactional
    public MeResponse getMe() {
        String username = getCurrentUsernameOrThrow();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        // 기존: return MeResponse.from(user);
        // 변경: 상세 프로필 조립 메서드로 위임
        return getMyProfile(user.getId());
    }

    // ===================== 상세 조립: 기본 인적 + 공동구매 + 주문 =====================
    @Transactional
    public MeResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        // 1) 내가 '참여'한 공동구매 (N+1 방지 fetch-join)
        List<GroupPurchaseParticipant> myParticipations = gppRepository.findDeepByUserId(userId);

        // 2) 내가 '개설'한 공동구매 (상점-오너 기준)
        List<GroupPurchase> myOwned = gpRepository.findOwnedByUserId(userId);

        // 3) 내가 '구매자'로 한 모든 주문 (아이템/상품/이미지까지 fetch)
        List<Order> myOrders = orderRepository.findDeepByBuyerId(userId);

        // 4) 공구별 참여 인원 수 집계 → 배송비(5000/n) 계산에 사용
        Set<Long> gpIds = new HashSet<>();
        myParticipations.stream()
                .map(GroupPurchaseParticipant::getGroupPurchase)
                .filter(Objects::nonNull)
                .map(GroupPurchase::getId)
                .filter(Objects::nonNull)
                .forEach(gpIds::add);
        myOwned.stream()
                .map(GroupPurchase::getId)
                .filter(Objects::nonNull)
                .forEach(gpIds::add);

        Map<Long, Integer> participantCountByGpId = new HashMap<>();
        if (!gpIds.isEmpty()) {
            for (Object[] row : gppRepository.countByGroupPurchaseIds(gpIds)) {
                Long gpId = (Long) row[0];
                Number cnt = (Number) row[1];
                participantCountByGpId.put(gpId, (cnt != null) ? cnt.intValue() : 1);
            }
        }

        // 5) DTO 변환 (이미지 선택 + 배송비 5000/n 로직은 MeResponse 내부에서 처리)
        return MeResponse.of(user, myParticipations, myOwned, myOrders, participantCountByGpId);
    }

    private String getCurrentUsernameOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new UnauthenticatedException("유효하지 않거나 만료된 토큰입니다.");
        }
        return auth.getName();
    }

    // ===================== 유효성/정규화 =====================
    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-z0-9_]{4,20}$");
    }
    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{8,64}$");
    }
    private boolean isValidRole(String role) {
        return "consumer".equalsIgnoreCase(role) || "seller".equalsIgnoreCase(role);
    }
    private String normalizePhone(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\D", "");
    }
    private boolean isValidPhone(String digitsOnly) {
        return digitsOnly != null && digitsOnly.matches("^\\d{10,11}$");
    }
    private String normalizeBusinessNumber(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\D", "");
    }
    private boolean isValidBusinessNumber(String businessNumber) {
        return businessNumber != null && businessNumber.matches("^\\d{10}$");
    }

    // ── 판매자 전용 추가 유효성/정규화 ──
    private String normalizeShopName(String raw) {
        if (raw == null) return "";
        return raw.trim();
    }
    private boolean isValidShopName(String name) {
        // 공백 제외 1~100자
        return name != null && name.length() >= 1 && name.length() <= 100;
    }
    private String normalizeAccountNumber(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\D", "");
    }
    private boolean isValidAccountNumber(String digitsOnly) {
        // 국내 은행 계좌 포맷 단순화: 숫자 6~20자리 허용
        return digitsOnly != null && digitsOnly.matches("^\\d{6,20}$");
    }
    private boolean isValidBankOrHolder(String s) {
        // 한글/영문/숫자/공백/(),-_. 1~100자
        return s != null && s.matches("^[0-9A-Za-z가-힣 ()\\-_.]{1,100}$");
    }
    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
