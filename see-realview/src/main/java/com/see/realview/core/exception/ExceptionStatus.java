package com.see.realview.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionStatus {
    // 공통 에러 1000번
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 1000, "인증되지 않았습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 1001, "권한이 없습니다."),
    // 유저 에러 2000번
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, 2000, "이미 존재하는 이메일입니다."),
    PASSWORD_NOT_EQUALS(HttpStatus.BAD_REQUEST, 2001, "비밀번호가 일치하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, 2002, "존재하지 않는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 2003, "비밀번호가 일치하지 않습니다."),

    // 토큰 에러 2100번
    TOKEN_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2100, "토큰 파싱 과정에서 오류가 발생했습니다."),
    SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, 2101, "잘못된 토큰입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2101, "액세스 토큰이 만료되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, 2102, "토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, 2103, "리프래시 토큰이 존재하지 않습니다"),
    TOKEN_PAIR_NOT_MATCH(HttpStatus.UNAUTHORIZED, 2104, "토큰쌍이 일치하지 않습니다.")
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
