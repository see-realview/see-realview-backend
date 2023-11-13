package com.see.realview.core.response;

public record ErrorData(
        int status,
        String message
) {
}
