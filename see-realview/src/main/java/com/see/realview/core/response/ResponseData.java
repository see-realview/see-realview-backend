package com.see.realview.core.response;

public record ResponseData<T>(
        Boolean success,
        T contents,
        ErrorData error
) {
}
