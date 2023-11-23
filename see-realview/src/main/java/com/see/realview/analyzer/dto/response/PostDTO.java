package com.see.realview.analyzer.dto.response;

public record PostDTO(
        String url,
        String title,
        String description,
        String date,
        String bloggerName,
        Boolean advertisement,
        Long recommendationCount
) {
}
