package com.see.realview.report.dto.request;

public record BugReportRequest(
        String title,
        String content
) implements ReportRequest {
}
