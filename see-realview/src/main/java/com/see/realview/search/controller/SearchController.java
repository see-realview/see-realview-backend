package com.see.realview.search.controller;

import com.see.realview._core.response.Response;
import com.see.realview.search.dto.response.AnalyzeResponse;
import com.see.realview.search.service.PostAnalyzer;
import com.see.realview.search.dto.request.KeywordSearchRequest;
import com.see.realview.search.dto.response.NaverSearchResponse;
import com.see.realview.search.service.NaverSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final NaverSearcher naverSearcher;

    private final PostAnalyzer postAnalyzer;


    public SearchController(@Autowired NaverSearcher naverSearcher,
                            @Autowired PostAnalyzer postAnalyzer) {
        this.naverSearcher = naverSearcher;
        this.postAnalyzer = postAnalyzer;
    }

    @GetMapping("")
    public ResponseEntity<?> searchKeyword(@RequestParam String keyword, @RequestParam(defaultValue = "1") Long cursor) {
        KeywordSearchRequest request = new KeywordSearchRequest(keyword, cursor);
        NaverSearchResponse searchResponse = naverSearcher.search(request);
        AnalyzeResponse responses = postAnalyzer.analyze(searchResponse);
        return ResponseEntity.ok().body(Response.success(responses));
    }
}
