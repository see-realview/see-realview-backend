package com.see.realview.analyzer.service;

import com.see.realview.analyzer.dto.request.AnalyzeRequest;
import com.see.realview.analyzer.dto.request.ImageParseRequest;
import com.see.realview.analyzer.dto.response.AnalyzeResponse;
import com.see.realview.analyzer.dto.response.PostDTO;
import com.see.realview.google.service.GoogleVisionAPI;
import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.service.ParsedImageServiceImpl;
import com.see.realview.search.dto.response.NaverSearchResponse;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostAnalyzer {

    private final RequestConverter requestConverter;

    private final HtmlParser htmlParser;

    private final TextParser textParser;

    private final GoogleVisionAPI googleVisionAPI;

    private final ParsedImageServiceImpl parsedImageService;


    public PostAnalyzer(@Autowired RequestConverter requestConverter,
                        @Autowired HtmlParser htmlParser,
                        @Autowired TextParser textParser,
                        @Autowired GoogleVisionAPI googleVisionAPI,
                        @Autowired ParsedImageServiceImpl parsedImageService) {
        this.requestConverter = requestConverter;
        this.htmlParser = htmlParser;
        this.textParser = textParser;
        this.googleVisionAPI = googleVisionAPI;
        this.parsedImageService = parsedImageService;
    }

    public AnalyzeResponse analyze(NaverSearchResponse response) {
        List<AnalyzeRequest> analyzeRequests = requestConverter.convert(response);

        List<ImageParseRequest> imageParseRequests = analyzeRequests
                .stream()
                .parallel()
                .map(request -> {
                    Optional<Elements> elements = htmlParser.parse(request);

                    if (elements.isEmpty()) {
                        return new ImageParseRequest(request, false, null, false);
                    }

                    Elements components = elements.get();

                    String text = components.text();
                    System.out.println(request.link() + "\t " + text);
                    Boolean advertisement = textParser.analyzePostText(text);
                    if (advertisement) {
                        return new ImageParseRequest(request, false, null, true);
                    }

                    Elements images = components.select("img");
                    Element image = images.get(images.size() - 1);

                    String url = image.attr("src");
                    String rawURL = url.replaceAll("\\\\?.*$", "");

                    Optional<CachedImage> cachedImage = parsedImageService.isAlreadyParsedImage(rawURL);
                    if (cachedImage.isPresent()) {
                        return new ImageParseRequest(request, false, null, cachedImage.get().data().advertisement());
                    }

                    if (url.contains("static.map") || // 지도 정보 제외
                            url.contains("dthumb-phinf.pstatic.net") || // 썸네일 사진 제외
                            url.contains("postfiles.pstatic.net") || // 블로그 이미지 제외
                            url.contains(".gif")) { // GIF 파일 제외
                        return new ImageParseRequest(request, false, null, false);
                    }

                    return new ImageParseRequest(request, true, url, false);
                })
                .toList();

        List<PostDTO> analyzePostResponse = googleVisionAPI.call(imageParseRequests);
        Long cursor = response.start() + analyzePostResponse.size();
        return new AnalyzeResponse(cursor, analyzePostResponse);
    }
}
