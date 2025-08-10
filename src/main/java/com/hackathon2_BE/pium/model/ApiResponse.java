package com.hackathon2_BE.pium.model;

public class ApiResponse {
    private String message;
    private Object data;

    // 생성자
    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    // Getter/Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
