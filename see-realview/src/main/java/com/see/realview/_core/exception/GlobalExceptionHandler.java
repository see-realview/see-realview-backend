package com.see.realview._core.exception;

import com.see.realview._core.exception.client.BadRequestException;
import com.see.realview._core.exception.client.ForbiddenException;
import com.see.realview._core.exception.client.NotFoundException;
import com.see.realview._core.exception.client.UnauthorizedException;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview._core.response.ErrorData;
import com.see.realview._core.response.Response;
import com.see.realview._core.response.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getAllErrors().get(0).getDefaultMessage();
        int code = ExceptionStatus.INVALID_METHOD_ARGUMENTS_ERROR.getCode();

        ErrorData errorData = new ErrorData(code, message);
        ResponseData<?> responseData = new ResponseData<>(false, null, errorData);

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(responseData, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception exception) {
        exception.printStackTrace();
        CustomException customException = new ServerException(ExceptionStatus.INTERNAL_SERVER_ERROR);
        return createExceptionResponseData(customException);
    }
}
