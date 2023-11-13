package com.see.realview.core.exception;

import com.see.realview.core.response.ResponseData;
import org.springframework.http.HttpStatus;

public interface CustomException {
    ResponseData<?> body();
    HttpStatus status();
    int code();
}
