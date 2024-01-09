package com.see.realview.search.service;

import com.mysema.commons.lang.Pair;
import com.see.realview.image.entity.ParsedImage;
import com.see.realview.search.dto.request.AnalyzeRequest;
import com.see.realview.search.dto.request.ImageParseRequest;
import com.see.realview.search.dto.response.AnalyzeResponse;
import com.see.realview.search.dto.response.PostDTO;
import com.see.realview.google.service.GoogleVisionAPI;
import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.dto.ImageData;
import com.see.realview.image.service.ParsedImageService;
import com.see.realview.search.dto.response.NaverSearchResponse;
import com.see.realview.search.entity.SearchItem;
import com.see.realview.search.repository.SearchItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PostAnalyzer {

    private final RequestConverter requestConverter;

    private final HtmlParser htmlParser;

    private final TextAnalyzer textAnalyzer;

    private final GoogleVisionAPI googleVisionAPI;

    private final ParsedImageService parsedImageService;

    private final SearchItemRepository searchItemRepository;


    public PostAnalyzer(@Autowired RequestConverter requestConverter,
                        @Autowired HtmlParser htmlParser,
                        @Autowired TextAnalyzer textAnalyzer,
                        @Autowired GoogleVisionAPI googleVisionAPI,
                        @Autowired ParsedImageService parsedImageService,
                        @Autowired SearchItemRepository searchItemRepository) {
        this.requestConverter = requestConverter;
        this.htmlParser = htmlParser;
        this.textAnalyzer = textAnalyzer;
        this.googleVisionAPI = googleVisionAPI;
        this.parsedImageService = parsedImageService;
        this.searchItemRepository = searchItemRepository;
    }

    public AnalyzeResponse analyze(NaverSearchResponse searchResponse) {
        List<AnalyzeRequest> analyzeRequests = requestConverter.createPostAnalyzeRequest(searchResponse);
        Map<String, Boolean> result = new HashMap<>();

        analyzeRequests.forEach(analyzeRequest -> {
            result.put(analyzeRequest.link(), false);
        });


        // 모델 학습 데이터 저장을 위한 임시 배열
        List<Pair<String, String>> texts = new ArrayList<>();

        List<ImageParseRequest> imageParseRequests = new ArrayList<>();

        analyzeRequests
                .stream()
                .parallel()
                .forEach(request -> {
                    Optional<Elements> elements = htmlParser.parse(request);
                    if (elements.isEmpty()) {
                        return;
                    }

                    Elements components = elements.get();
                    String text = components.text();
                    Boolean advertisement = textAnalyzer.analyzePostText(text);
                    texts.add(new Pair<>(request.link(), text));

                    if (advertisement) {
                        log.debug("텍스트에서 광고 확인됨 | " + request.link());
                        result.put(request.link(), true);
                        return;
                    }

                    Elements images = components.select("img");

                    if (images.size() == 0) {
                        return;
                    }

                    Element image = images.get(images.size() - 1);
                    String url = image.attr("src");
                    String rawURL = url.replaceAll("\\?.*$", "");

                    Optional<CachedImage> cachedImage = parsedImageService.isAlreadyParsedImage(rawURL);
                    if (cachedImage.isPresent()) {
                        ImageData cachedData = cachedImage.get().data();
                        log.debug("이미지 캐싱 정보 확인됨 | " + request.link());
                        result.put(request.link(), cachedData.advertisement());
                        return;
                    }

                    if (url.contains("static.map") || // 지도 정보 제외
                            url.contains("dthumb-phinf.pstatic.net") || // 썸네일 사진 제외
                            url.contains(".gif")) { // GIF 파일 제외
                        result.put(request.link(), false);
                        return;
                    }

                    imageParseRequests.add(
                            new ImageParseRequest(request.link(), url)
                    );
                });

        List<String> visionResponse = googleVisionAPI.call(imageParseRequests);
        Queue<String> resultQueue = new LinkedList<>(visionResponse);
        List<ParsedImage> images = new ArrayList<>();

        imageParseRequests.forEach(request -> {
            String text = resultQueue.poll();
            log.debug(request.imageLink() + " | " + text);
            boolean advertisement = textAnalyzer.analyzeImageText(text);

            result.put(request.postLink(), advertisement);

            String url = request.imageLink();
            String rawURL = url.replaceAll("\\?.*$", "");
            images.add(ParsedImage.of(rawURL, advertisement));
        });

        parsedImageService.saveAll(images);
        parsedImageService.rebase();

        List<PostDTO> responses = searchResponse.items()
                .stream()
                .map(naverSearchItem -> {
                    String link = naverSearchItem.link();
                    Boolean advertisement = result.get(link);
                    return PostDTO.of(naverSearchItem, advertisement, 0L);
                })
                .toList();

        List<SearchItem> searchItems = responses
                .stream()
                .map(postDTO -> {
                    String text = texts
                            .stream()
                            .filter(iter -> iter.getFirst().equals(postDTO.link()))
                            .findFirst().orElseThrow()
                            .getSecond();

                    return SearchItem.of(postDTO, text);
                })
                .toList();
        searchItemRepository.saveAll(searchItems);

        Long cursor = searchResponse.start() + responses.size();
        return new AnalyzeResponse(cursor, responses);
    }
}
