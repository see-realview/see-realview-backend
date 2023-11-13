package com.see.realview.user.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
