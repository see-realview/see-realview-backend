package com.see.realview.analyzer.dto.response;

import java.util.List;

public record AnalyzeResponse(
        Long cursor,
        List<PostDTO> data
) {
}
