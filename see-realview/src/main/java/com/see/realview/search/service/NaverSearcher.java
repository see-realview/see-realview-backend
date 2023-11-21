package com.see.realview.search.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.search.dto.request.KeywordSearchRequest;
import com.see.realview.search.dto.response.NaverSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Component
public class NaverSearcher {

    private final WebClient naverWebClient;

    public static int SEARCH_COUNT = 10;


    public NaverSearcher(@Autowired WebClient naverWebClient) {
        this.naverWebClient = naverWebClient;
    }

    public NaverSearchResponse search(KeywordSearchRequest request) {
        return getSearchResponse(request)
                .orElseThrow(() -> new ServerException(ExceptionStatus.NAVER_SEARCH_ERROR));
    }

    private Optional<NaverSearchResponse> getSearchResponse(KeywordSearchRequest request) {
        return naverWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", request.keyword())
                        .queryParam("display", SEARCH_COUNT)
                        .queryParam("start", request.cursor())
                        .build())
                .retrieve()
                .bodyToFlux(NaverSearchResponse.class)
                .toStream()
                .findFirst();
    }
}
