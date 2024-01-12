package com.see.realview.search.service;

import org.springframework.stereotype.Component;

@Component
public class TextAnalyzer {

    public Boolean analyzePostText(String text) {
        text = text.trim();
        return (text.contains("제공받") || text.contains("지원받")) ||
                (
                        (text.contains("업체") || text.contains("체험단") || text.contains("원고료") || text.contains("서비스"))
                        && (text.contains("지원") || text.contains("제공"))
                        && text.contains("받아")
                );
    }

    public Boolean analyzeImageText(String text) {
        if (text == null) {
            return false;
        }

        text = text.trim();
        return (text.contains("제공받") || text.contains("지원받")) || text.contains("협찬받") ||
                (
                        (text.contains("업체") || text.contains("체험단") || text.contains("원고료") || text.contains("서비스"))
                                && (text.contains("지원") || text.contains("제공"))
                                && text.contains("받아")
                );
    }
}
