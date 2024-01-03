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
        String date = parseRequest.request().date();
        String formattedDate = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);

        return new PostDTO(
                parseRequest.request().link(),
                parseRequest.request().title(),
                parseRequest.request().description(),
                formattedDate,
                parseRequest.request().bloggerName(),
                advertisement,
                0L
        );
    }
}
