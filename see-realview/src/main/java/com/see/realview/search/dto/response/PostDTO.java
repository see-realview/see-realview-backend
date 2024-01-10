package com.see.realview.search.dto.response;

import com.see.realview.search.dto.request.ImageParseRequest;

public record PostDTO(
        String link,
        String title,
        String description,
        String date,
        String bloggerName,
        Boolean advertisement,
        Long recommendationCount
) {

    public static PostDTO of(NaverSearchItem item, Boolean advertisement, Long recommendationCount) {
        String date = item.postdate();
        String formattedDate = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);

        return new PostDTO(
                item.link(),
                item.title(),
                item.description(),
                formattedDate,
                item.bloggername(),
                advertisement,
                0L
        );
    }
}
