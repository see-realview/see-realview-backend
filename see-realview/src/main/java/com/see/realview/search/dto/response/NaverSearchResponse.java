package com.see.realview.search.dto.response;

import java.util.List;

public record NaverSearchResponse(
        String lastBuildDate,
        Long total,
        Long start,
        Long display,
        List<NaverSearchItem> items
) {
}
