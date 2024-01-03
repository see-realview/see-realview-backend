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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
                .filter(Objects::nonNull)
                .toList();
    }

    public List<RequestIterator> getRequestIterators(List<ImageParseRequest> requests, List<RequestFeature> requestFeatures) {
        return IntStream
                .range(0, requests.size())
                .parallel()
                .mapToObj(idx -> {
                    ImageParseRequest request = requests.get(idx);
                    String url = request.url();

                    if (!request.required()) {
                        url = null;
                    }

                    RequestImage image = new RequestImage(getEncodedImageFromURL(url));
                    RequestItem requestItem = new RequestItem(image, requestFeatures);
                    return new RequestIterator(requestItem, idx);
                })
                .toList();
    }

    public List<RequestItem> getRequestItems(List<RequestIterator> requestPairs) {
        return requestPairs
                .stream()
                .sorted(Comparator.comparing(RequestIterator::index).reversed())
                .map(RequestIterator::item)
                .filter(item -> item.image().content() != null && !item.image().content().equals(""))
                .toList();
    }

    private static String getEncodedImageFromURL(String url) {
        try {
            if (url == null) {
                return null;
            }

            URL imageURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            byte[] imageBytes = connection.getInputStream().readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
        catch (IOException exception) {
            exception.printStackTrace();
            throw new ServerException(ExceptionStatus.IMAGE_PARSING_ERROR);
        }
    }
}
