package com.see.realview.report.service;

import com.see.realview.report.dto.request.ReportRequest;
import com.see.realview.report.service.constant.ReportType;

public interface ReportService {

    String BUG_REPORT_PREFIX = "[see-realview][BUG]";


    void send(ReportType type, ReportRequest request);
}
