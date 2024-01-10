package com.see.realview.report.service.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportType {
    BUG("[see-realview][BUG]");


    private final String type;

    public String toString() {
        return type;
    }
}
