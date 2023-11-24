package com.see.realview._core.response;

public record ResponseData<T>(
        Boolean success,
        T contents,
        ErrorData error
) {
}
