package com.see.realview.user.dto.request;

public record RegisterRequest(
        String email,
        String username,
        String password,
        String password2
) {
}
