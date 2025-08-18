package com.hackathon2_BE.pium.payment;

public interface PaymentGateway {
    InitResult init(String orderId, int amount, String method, String successUrl, String failUrl, String idemKey);
    record InitResult(String provider, String method, String paymentToken, String redirectUrl){}
}
