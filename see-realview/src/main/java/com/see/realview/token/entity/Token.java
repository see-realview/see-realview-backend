package com.see.realview.token.entity;

public record Token(
        String accessToken,

        String refreshToken

) {

    public Boolean equals(String accessToken, String refreshToken) {
        return this.accessToken.equals(accessToken) && this.refreshToken.equals(refreshToken);
    }
}
