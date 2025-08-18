package com.hackathon2_BE.pium.exception;

import java.util.List;

public class ConflictException extends RuntimeException {
    private final List<?> errors;

    public ConflictException(String message, List<?> errors) {
        super(message);
        this.errors = errors;
    }

    public List<?> getErrors() {
        return errors;
    }
}
