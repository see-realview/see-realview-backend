package com.see.realview._core.exception.client;

import com.see.realview._core.exception.CustomException;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.response.ErrorData;
import com.see.realview._core.response.ResponseData;
import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException implements CustomException {

    private final ExceptionStatus exception;


    public BadRequestException(ExceptionStatus exception) {
        super(exception.getMessage());
        this.exception = exception;
    }

    @Override
    public ResponseData<?> body() {
        ErrorData errorData = new ErrorData(exception.getCode(), exception.getMessage());
        return new ResponseData<>(false, null, errorData);
    }

    @Override
    public HttpStatus status() {
        return exception.getStatus();
    }

    @Override
    public int code() {
        return exception.getCode();
    }
}
