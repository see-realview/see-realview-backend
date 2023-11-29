package com.see.realview.analyzer.dto.request;

public record ImageParseRequest(
        AnalyzeRequest request,
        Boolean required,
        String url,
        Boolean advertisement
) {
}
