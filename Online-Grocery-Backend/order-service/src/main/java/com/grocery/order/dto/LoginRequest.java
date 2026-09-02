package com.grocery.order.dto;

public record LoginRequest(
        String email,
        String password
) {
}