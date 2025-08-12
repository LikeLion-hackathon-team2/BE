package com.hackathon2_BE.pium.exception;

public class RateLimitException extends RuntimeException{
    public RateLimitException(String message) {
        super(message);
    }
}
