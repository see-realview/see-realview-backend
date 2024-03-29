package com.see.realview.search.service;

import com.see.realview._core.utils.ImageDownloader;
import com.see.realview.google.dto.RequestFeature;
import com.see.realview.google.dto.RequestImage;
import com.see.realview.google.dto.RequestItem;
import com.see.realview.google.dto.RequestIterator;
import com.see.realview.search.dto.request.AnalyzeRequest;
import com.see.realview.search.dto.request.ImageParseRequest;
import com.see.realview.search.dto.response.NaverSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Component
@Slf4j
public class RequestConverter {

    private final ImageDownloader imageDownloader;


    public RequestConverter(@Autowired ImageDownloader imageDownloader) {
        this.imageDownloader = imageDownloader;
    }

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

    public RequestIterator processRequest(ImageParseRequest request, List<RequestFeature> features, int idx) {
        RequestImage image = new RequestImage(imageDownloader.getEncodedImageFromURL(request.imageLink()));
        RequestItem item = new RequestItem(image, features);
        return new RequestIterator(item, idx);
    }

    public List<RequestIterator> getRequestIterators(List<ImageParseRequest> requests, List<RequestFeature> features) {
        List<CompletableFuture<RequestIterator>> futures = IntStream.range(0, requests.size())
                .mapToObj(idx -> CompletableFuture.supplyAsync(() -> processRequest(requests.get(idx), features, idx)))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
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
}
