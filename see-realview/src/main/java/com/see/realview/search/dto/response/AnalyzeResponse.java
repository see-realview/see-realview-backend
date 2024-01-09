package com.see.realview.search.dto.response;

import java.util.List;

public record AnalyzeResponse(
        Long cursor,
        List<PostDTO> data
) {
}
