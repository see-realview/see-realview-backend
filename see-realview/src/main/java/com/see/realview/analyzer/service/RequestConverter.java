package com.see.realview.analyzer.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.analyzer.dto.request.AnalyzeRequest;
import com.see.realview.analyzer.dto.request.ImageParseRequest;
import com.see.realview.google.dto.RequestFeature;
import com.see.realview.google.dto.RequestImage;
import com.see.realview.google.dto.RequestItem;
import com.see.realview.google.dto.RequestIterator;
import com.see.realview.search.dto.response.NaverSearchResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Component
public class RequestConverter {

    public List<AnalyzeRequest> convert(NaverSearchResponse response) {
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

    public List<RequestIterator> getRequestIterators(List<ImageParseRequest> requests, List<RequestFeature> requestFeatures) {
        return IntStream
                .range(0, requests.size() - 1)
                .parallel()
                .mapToObj(idx -> {
                    ImageParseRequest request = requests.get(idx);
                    RequestImage image = new RequestImage(getEncodedImageFromURL(request.url()));
                    RequestItem requestItem = new RequestItem(image, requestFeatures);
                    return new RequestIterator(requestItem, idx);
                })
                .toList();
    }

    private static String getEncodedImageFromURL(String url) {
        try {
            if (url == null) {
                return null;
            }

            URL imageURL = new URL(url);
            byte[] imageBytes = imageURL.openConnection().getInputStream().readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
        catch (IOException exception) {
            throw new ServerException(ExceptionStatus.IMAGE_PARSING_ERROR);
        }
    }

    public List<RequestItem> getRequestItems(List<RequestIterator> requestPairs) {
        return requestPairs
                .stream()
                .sorted(Comparator.comparing(RequestIterator::index))
                .map(RequestIterator::item)
                .filter(item -> item.image().content() != null)
                .toList();
    }
}