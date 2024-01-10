package com.see.realview.search.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.search.dto.request.AnalyzeRequest;
import com.see.realview.search.dto.request.ImageParseRequest;
import com.see.realview.google.dto.RequestFeature;
import com.see.realview.google.dto.RequestImage;
import com.see.realview.google.dto.RequestItem;
import com.see.realview.google.dto.RequestIterator;
import com.see.realview.search.dto.response.NaverSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Component
@Slf4j
public class RequestConverter {

    public List<AnalyzeRequest> createPostAnalyzeRequest(NaverSearchResponse response) {
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
                .filter(Objects::nonNull)
                .toList();
    }

    public List<RequestIterator> getRequestIterators(List<ImageParseRequest> requests, List<RequestFeature> features) {
        return IntStream
                .range(0, requests.size())
                .parallel()
                .mapToObj(idx -> {
                    ImageParseRequest request = requests.get(idx);
                    try {
                        RequestImage image = new RequestImage(getEncodedImageFromURL(request.imageLink()));
                        RequestItem item = new RequestItem(image, features);
                        return new RequestIterator(item, idx);
                    } catch (IOException exception) {
                        log.debug("OCR request 변환 과정 오류"
                                + "\n - 포스트 링크 : " + request.postLink()
                                + "\n - 이미지 링크 : " + request.imageLink());
                        throw new ServerException(ExceptionStatus.IMAGE_PARSING_ERROR);
                    }
                })
                .toList();
    }

    public List<RequestItem> getRequestItems(List<RequestIterator> requestPairs) {
        return requestPairs
                .stream()
                .sorted(Comparator.comparing(RequestIterator::index))
                .map(RequestIterator::item)
                .filter(item -> item.image().content() != null && !item.image().content().equals(""))
                .toList();
    }

    private static String getEncodedImageFromURL(String url) throws IOException {
        if (url == null) {
            return null;
        }

        String encodedURL = getEncodedURL(url);
        URL imageURL = new URL(encodedURL);
        HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        byte[] imageBytes = connection.getInputStream().readAllBytes();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private static String getEncodedURL(String url) {
        String[] parts = Arrays.stream(url.split("/"))
                .map(s -> s.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*") ? URLEncoder.encode(s, StandardCharsets.UTF_8) : s)
                .toArray(String[]::new);

        return String.join("/", parts);
    }
}
