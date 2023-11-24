package com.see.realview._core.exception;

import com.see.realview._core.exception.client.BadRequestException;
import com.see.realview._core.exception.client.ForbiddenException;
import com.see.realview._core.exception.client.NotFoundException;
import com.see.realview._core.exception.client.UnauthorizedException;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview._core.response.Response;
import com.see.realview._core.response.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static ResponseEntity<? extends ResponseData<?>> createExceptionResponseData(CustomException exception) {
        HttpStatus status = exception.status();
        ResponseData<?> data = exception.body();
        return new ResponseEntity<>(data, status);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequest(BadRequestException exception) {
        return createExceptionResponseData(exception);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> unAuthorized(UnauthorizedException exception) {
        return createExceptionResponseData(exception);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> forbidden(ForbiddenException exception) {
        return createExceptionResponseData(exception);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFound(NotFoundException exception) {
        return createExceptionResponseData(exception);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<?> serverError(ServerException exception) {
        return createExceptionResponseData(exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception exception) {
        exception.printStackTrace();
        CustomException customException = new ServerException(ExceptionStatus.INTERNAL_SERVER_ERROR);
        return createExceptionResponseData(customException);
    }
}
