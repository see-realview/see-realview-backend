package com.see.realview.analyzer.dto.response;

import com.see.realview.analyzer.dto.request.ImageParseRequest;

public record PostDTO(
        String url,
        String title,
        String description,
        String date,
        String bloggerName,
        Boolean advertisement,
        Long recommendationCount
) {
    public static PostDTO of(ImageParseRequest parseRequest, Boolean advertisement, Long recommendationCount) {
        return new PostDTO(
                parseRequest.request().link(),
                parseRequest.request().title(),
                parseRequest.request().description(),
                parseRequest.request().date(),
                parseRequest.request().bloggerName(),
                advertisement,
                0L
        );
    }
}
