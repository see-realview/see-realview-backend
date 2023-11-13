package com.see.realview.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum BaseException {
    // 공통 에러 1000번

    // 유저 에러 2000번
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, 2000, "이미 존재하는 이메일입니다."),
    PASSWORD_NOT_EQUALS(HttpStatus.BAD_REQUEST, 2001, "비밀번호가 일치하지 않습니다.")
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
