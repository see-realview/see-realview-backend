package com.see.realview.report.controller;

import com.see.realview._core.response.Response;
import com.see.realview.report.dto.request.BugReportRequest;
import com.see.realview.report.service.ReportService;
import com.see.realview.report.service.constant.ReportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;


    public ReportController(@Autowired ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/bug")
    public ResponseEntity<?> sendBugReport(@RequestBody BugReportRequest request) {
        reportService.send(ReportType.BUG, request);
        return ResponseEntity.ok().body(Response.success(null));
    }
}
