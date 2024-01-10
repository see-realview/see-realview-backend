package com.see.realview.report.service;

import com.see.realview.report.dto.request.ReportRequest;
import com.see.realview.report.service.constant.ReportType;

public interface ReportService {

    void send(ReportType type, ReportRequest request);

    String replaceExpletives(String content);
}
