package com.see.realview.analyzer.service;

import com.see.realview.analyzer.dto.request.AnalyzeRequest;
import com.see.realview.search.dto.response.NaverSearchResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AnalyzeRequestConverter {

    public List<AnalyzeRequest> converte(NaverSearchResponse response) {
        return response.items()
                .stream()
                .map(naverSearchItem -> {
                    Matcher matcher = Pattern.compile("/([^/]+)$").matcher(naverSearchItem.bloggerlink());
                    String blogURL = naverSearchItem.link().replace("\\", "");
                    String bloggerId = "";

                    if (matcher.find()) {
                        bloggerId = matcher.group(1);
                    }

                    if (!blogURL.contains("blog.naver.com")) {
                        return null;
                    }

                    return new AnalyzeRequest(
                            naverSearchItem.link(),
                            naverSearchItem.title(),
                            naverSearchItem.description(),
                            naverSearchItem.postdate(),
                            bloggerId,
                            naverSearchItem.bloggername()
                    );
                })
                .toList();
    }
}
