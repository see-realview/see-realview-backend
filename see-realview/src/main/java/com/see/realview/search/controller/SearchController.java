package com.see.realview.search.controller;

import com.see.realview._core.response.Response;
import com.see.realview.analyzer.dto.response.AnalyzeResponse;
import com.see.realview.analyzer.service.PostAnalyzer;
import com.see.realview.search.dto.request.KeywordSearchRequest;
import com.see.realview.search.dto.response.NaverSearchResponse;
import com.see.realview.search.service.NaverSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final NaverSearcher naverSearcher;

    private final PostAnalyzer postAnalyzer;


    public SearchController(@Autowired NaverSearcher naverSearcher,
                            @Autowired PostAnalyzer postAnalyzer) {
        this.naverSearcher = naverSearcher;
        this.postAnalyzer = postAnalyzer;
    }

    @GetMapping("")
    public ResponseEntity<?> searchKeyword(@RequestBody KeywordSearchRequest request) {
        NaverSearchResponse searchResponse = naverSearcher.search(request);
        AnalyzeResponse responses = postAnalyzer.analyze(searchResponse);
        return ResponseEntity.ok().body(Response.success(responses));
    }
}
