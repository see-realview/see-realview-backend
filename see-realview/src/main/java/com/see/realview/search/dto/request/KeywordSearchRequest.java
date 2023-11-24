package com.see.realview.search.dto.request;

public record KeywordSearchRequest(
        String keyword,
        Long cursor
) {
}
