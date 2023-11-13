package com.see.realview.user.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    USER("유저"),
    ADMIN("관리자");

    @Getter
    private final String roleName;
}
