package com.see.realview._core.exception;

import com.see.realview._core.response.ResponseData;
import org.springframework.http.HttpStatus;

public interface CustomException {
    ResponseData<?> body();
    HttpStatus status();
    int code();
}
