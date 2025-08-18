package com.hackathon2_BE.pium.payment;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("naverMockGateway")
public class NaverMockGateway implements PaymentGateway{
    @Override
    public InitResult init(String orderId, int amount, String method, String successUrl, String failUrl, String idemKey) {
        String token = "pay_tok_naver_" + UUID.randomUUID();
        String redirect = "http://localhost:8080/mockpay/checkout?token="+token+"&brand=naver";
        return new InitResult("naver", method, token, redirect);
    }
}
