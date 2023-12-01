package com.see.realview.code.entity;

public record EmailCode(
        String code,
        Boolean authenticated
) {
    public EmailCode updateEmailAuthenticated() {
        return new EmailCode(code, true);
    }
}
