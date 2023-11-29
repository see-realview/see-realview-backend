package com.see.realview.code.dto;

public record VerifyEmailRequest(
        String email,
        String code
) {
}
