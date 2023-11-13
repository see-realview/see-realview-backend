package com.see.realview.core.response;

public class Response {
    public static <T> ResponseData<T> success(T contents) {
        return new ResponseData<>(true, contents, null);
    }

    public static <T> ResponseData<T> error(int status, String message) {
        ErrorData errorData = new ErrorData(status, message);
        return new ResponseData<>(false, null, errorData);
    }
}
