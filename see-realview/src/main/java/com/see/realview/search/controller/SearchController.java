package com.see.realview.search.controller;

import com.see.realview._core.response.Response;
import com.see.realview._core.response.ResponseData;
import com.see.realview.search.dto.response.AnalyzeResponse;
import com.see.realview.search.service.PostAnalyzer;
import com.see.realview.search.dto.request.KeywordSearchRequest;
import com.see.realview.search.dto.response.NaverSearchResponse;
import com.see.realview.search.service.NaverSearcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/search")
@Slf4j
public class SearchController {

    private final NaverSearcher naverSearcher;

    private final PostAnalyzer postAnalyzer;


    public SearchController(@Autowired NaverSearcher naverSearcher,
                            @Autowired PostAnalyzer postAnalyzer) {
        this.naverSearcher = naverSearcher;
        this.postAnalyzer = postAnalyzer;
    }

    @GetMapping("")
    public CompletableFuture<ResponseEntity<?>> searchKeyword(@RequestParam String keyword, @RequestParam(defaultValue = "1") Long cursor) {
        log.debug("+---------------------------------------------+");
        log.debug("|               새로운 검색 요청               |");
        log.debug("+---------------------------------------------+");

        KeywordSearchRequest request = new KeywordSearchRequest(keyword, cursor);
        CompletableFuture<NaverSearchResponse> searchFuture = naverSearcher.search(request);
        CompletableFuture<AnalyzeResponse> response = searchFuture.thenCompose(postAnalyzer::analyze);

        return response.thenApply(result -> ResponseEntity.ok().body(Response.success(result)));
    }
}
