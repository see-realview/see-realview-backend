package com.see.realview._core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionStatus {
    // 공통 에러 1000번
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 1000, "인증되지 않았습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 1001, "권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1002, "서버에서 알 수 없는 에러가 발생했습니다."),
    DATA_CONVERSION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1003, "데이터 변환 과정에서 오류가 발생했습니다."),

    // 유저 에러 2000번
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, 2000, "이미 존재하는 이메일입니다."),
    PASSWORD_NOT_EQUALS(HttpStatus.BAD_REQUEST, 2001, "비밀번호가 일치하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, 2002, "존재하지 않는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 2003, "비밀번호가 일치하지 않습니다."),

    // 토큰 에러 2100번
    TOKEN_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2100, "토큰 파싱 과정에서 오류가 발생했습니다."),
    SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, 2101, "잘못된 토큰입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2102, "액세스 토큰이 만료되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, 2103, "토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, 2104, "리프래시 토큰이 존재하지 않습니다"),
    TOKEN_PAIR_NOT_MATCH(HttpStatus.UNAUTHORIZED, 2105, "토큰쌍이 일치하지 않습니다."),
    UNNECESSARY_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 2106, "액세스 토큰만 가지고 요청해주세요."),
    ACCESS_TOKEN_NOT_EXPIRED(HttpStatus.UNAUTHORIZED, 2107, "액세스 토큰이 유효하기 때문에 갱신을 할 수 없습니다."),

    // 이메일 인증 코드 에러 2200번
    EMAIL_CONTENT_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2200, "이메일 내용 생성 중 오류가 발생했습니다."),
    EMAIL_AUTHENTICATION_CODE_GENERATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2201, "이메일 인증 코드 생성 중 오류가 발생했습니다."),
    EMAIL_AUTHENTICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, 2202, "이메일 인증 코드를 찾을 수 없습니다."),
    EMAIL_AUTHENTICATION_CODE_NOT_MATCHED(HttpStatus.BAD_REQUEST, 2203, "이메일 인증 코드가 일치하지 않습니다."),

    // 네이버 검색 에러 3000번
    NAVER_SEARCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 3000, "네이버 검색 요청 중 에러가 발생했습니다."),

    // 포스트 분석 에러 4000번
    POST_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 4000, "포스트 파싱 중에 에러가 발생했습니다."),
    IMAGE_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 4001, "이미지 파싱 중에 에러가 발생했습니다."),

    // 이미지 캐싱 에러 5000번
    CACHED_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, 5001, "캐싱된 이미지가 존재하지 않습니다.")
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
