package com.see.realview.token.entity.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Header {
    AUTHORIZATION("Authorization"), REFRESH("Refresh");

    private final String field;

    public String value() {
        return field;
    }
}
