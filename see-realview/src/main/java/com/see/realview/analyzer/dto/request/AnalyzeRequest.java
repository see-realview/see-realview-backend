package com.see.realview.analyzer.dto.request;

public record AnalyzeRequest(
        String link,
        String title,
        String description,
        String date,
        String bloggerId,
        String bloggerName
) {
}
