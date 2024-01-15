package com.see.realview.search.dto.response;

import com.see.realview.search.dto.request.ImageParseRequest;

import java.util.List;

public record PostDTO(
        String link,
        String title,
        String description,
        String date,
        String bloggerName,
        Boolean advertisement,
        Long recommendationCount,
        List<String> images
) {

    public static PostDTO of(NaverSearchItem item, Boolean advertisement, Long recommendationCount, List<String> images) {
        String date = item.postdate();
        String formattedDate = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);

        return new PostDTO(
                item.link(),
                item.title(),
                item.description(),
                formattedDate,
                item.bloggername(),
                advertisement,
                0L,
                images
        );
    }
}
