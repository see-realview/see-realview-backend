package com.see.realview.report.service;

import com.see.realview.report.dto.request.ReportRequest;
import com.see.realview.report.service.constant.ReportType;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface ReportService {

    void send(ReportType type, ReportRequest request) throws MessagingException, UnsupportedEncodingException;

    String replaceExpletives(String content);
}
