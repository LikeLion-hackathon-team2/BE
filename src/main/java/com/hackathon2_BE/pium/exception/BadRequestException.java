package com.hackathon2_BE.pium.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String m) { super(m); }
}
