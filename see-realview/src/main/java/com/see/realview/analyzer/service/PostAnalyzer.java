package com.see.realview.analyzer.service;

import com.see.realview.analyzer.dto.request.AnalyzeRequest;
import com.see.realview.analyzer.dto.request.ImageParseRequest;
import com.see.realview.analyzer.dto.response.AnalyzeResponse;
import com.see.realview.analyzer.dto.response.PostDTO;
import com.see.realview.google.service.GoogleVisionAPI;
import com.see.realview.search.dto.response.NaverSearchResponse;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostAnalyzer {

    private final RequestConverter requestConverter;

    private final HtmlParser htmlParser;

    private final TextParser textParser;

    private final GoogleVisionAPI googleVisionAPI;


    public PostAnalyzer(@Autowired RequestConverter requestConverter,
                        @Autowired HtmlParser htmlParser,
                        @Autowired TextParser textParser,
                        @Autowired GoogleVisionAPI googleVisionAPI) {
        this.requestConverter = requestConverter;
        this.htmlParser = htmlParser;
        this.textParser = textParser;
        this.googleVisionAPI = googleVisionAPI;
    }

    public AnalyzeResponse analyze(NaverSearchResponse response) {
        List<AnalyzeRequest> analyzeRequests = requestConverter.convert(response);

        List<ImageParseRequest> imageParseRequests = analyzeRequests
                .stream()
                .parallel()
                .map(request -> {
                    Elements elements = htmlParser.parse(request);

                    String text = elements.text();
                    Boolean advertisement = textParser.analyze(text);
                    if (advertisement) {
                        return new ImageParseRequest(request, false, null);
                    }

                    Elements images = elements.select("img");
                    Element image = images.get(images.size() - 1);

                    return new ImageParseRequest(request, true, image.attr("src"));
                })
                .toList();

        List<PostDTO> analyzePostResponse = googleVisionAPI.call(imageParseRequests);
        Long cursor = response.start() + analyzePostResponse.size();
        return new AnalyzeResponse(cursor, analyzePostResponse);
    }
}
