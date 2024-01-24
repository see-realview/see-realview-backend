package com.see.realview._core.exception;

import com.see.realview._core.exception.client.BadRequestException;
import com.see.realview._core.exception.client.ForbiddenException;
import com.see.realview._core.exception.client.NotFoundException;
import com.see.realview._core.exception.client.UnauthorizedException;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview._core.response.ErrorData;
import com.see.realview._core.response.Response;
import com.see.realview._core.response.ResponseData;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler({ MethodArgumentNotValidException.class,
                        MethodArgumentTypeMismatchException.class })
    public ResponseEntity<?> methodArgumentNotValid(Exception exception) {
        String message = exception.getMessage();
        int code = ExceptionStatus.INVALID_METHOD_ARGUMENTS_ERROR.getCode();

        ErrorData errorData = new ErrorData(code, message);
        ResponseData<?> responseData = new ResponseData<>(false, null, errorData);

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(responseData, status);
    }

    @ExceptionHandler({ MessagingException.class,
                        UnsupportedEncodingException.class })
    public ResponseEntity<?> reportProcessException(Exception exception) {
        log.error("[exception handler]", exception);
        CustomException customException = new ServerException(ExceptionStatus.EMAIL_CONTENT_CREATE_ERROR);
        return createExceptionResponseData(customException);
    }

    @ExceptionHandler({ ExecutionException.class,
                        InterruptedException.class })
    public ResponseEntity<?> executionException(Exception exception) {
        log.error("[exception handler]", exception);
        CustomException customException = new ServerException(ExceptionStatus.THREAD_EXECUTION_ERROR);
        return createExceptionResponseData(customException);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> ioException(IOException exception) {
        log.error("[exception handler]", exception);
        CustomException customException = new ServerException(ExceptionStatus.IMAGE_PARSING_ERROR);
        return createExceptionResponseData(customException);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception exception) {
        log.error("[exception handler]", exception);
        CustomException customException = new ServerException(ExceptionStatus.INTERNAL_SERVER_ERROR);
        return createExceptionResponseData(customException);
    }
}
