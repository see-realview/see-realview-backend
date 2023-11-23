package com.see.realview.analyzer.service;

import org.springframework.stereotype.Component;

@Component
public class TextParser {

    public Boolean analyze(String text) {
        return (
                (
                        (text.contains("업체") || text.contains("체험단") || text.contains("원고료"))
                        && (text.contains("지원") || text.contains("제공"))
                        && text.contains("받아")
                )
                || text.contains("제공받") || text.contains("지원받"));
    }
}
