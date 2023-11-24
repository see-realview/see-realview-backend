package com.see.realview.analyzer.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.analyzer.dto.request.AnalyzeRequest;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HtmlParser {

    public Elements parse(AnalyzeRequest request) {
        try {
            String[] splits = request.link().split("/");
            String postId = splits[splits.length - 1];
            String postURL = "https://blog.naver.com/PostView.naver?blogId=" + request.bloggerId() + "&logNo=" + postId;

            Connection connection = Jsoup.connect(postURL);
            connection.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            Document document = connection.get();

            Elements items = document.select("#post-view" + postId + " > div > div > div.se-main-container");

            if (items.size() == 0) {
                items = document.select("#post-view" + postId + " > div > div.se-main-container");
            }

            return items;
        }
        catch (IOException exception) {
            throw new ServerException(ExceptionStatus.POST_PARSING_ERROR);
        }
    }
}
