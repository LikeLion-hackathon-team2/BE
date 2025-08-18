package com.hackathon2_BE.pium.payment;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("tossTestGateway")
@Primary
public class TossTestGateway implements PaymentGateway{
    @Override
    public InitResult init(String orderId, int amount, String method, String successUrl, String failUrl, String idemKey) {
        String token = "pay_tok_toss_" + UUID.randomUUID();
        String redirect = "https://js.tosspayments.com/sdk/redirect?mock=1&tok="+token; // 데모용 프리뷰 URL(예시)
        return new InitResult("toss", method, token, redirect);
    }
}
