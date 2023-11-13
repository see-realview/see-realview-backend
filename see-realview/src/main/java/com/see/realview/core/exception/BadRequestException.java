package com.see.realview.core.exception;

import com.see.realview.core.response.ErrorData;
import com.see.realview.core.response.ResponseData;
import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException implements CustomException {

    private final BaseException exception;


    public BadRequestException(BaseException exception) {
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
