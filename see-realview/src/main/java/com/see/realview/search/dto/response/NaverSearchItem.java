package com.see.realview.search.dto.response;

public record NaverSearchItem(
        String title,
        String link,
        String description,
        String bloggername,
        String bloggerlink,
        String postdate
) {
}
