package com.see.realview._core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.search.naver.id}")
    private String CLIENT_ID;

    @Value("${api.search.naver.secret}")
    private String CLIENT_SECRET;

    @Value("${api.search.naver.url}")
    private String REQUEST_URL;


    @Bean(name = "naverWebClient")
    public WebClient naverWebClient() {
        return WebClient.builder()
                .baseUrl(REQUEST_URL)
                .defaultHeader("X-Naver-Client-Id", CLIENT_ID)
                .defaultHeader("X-Naver-Client-Secret", CLIENT_SECRET)
                .build();
    }

    @Bean(name = "googleWebClient")
    public WebClient googleWebClient() {
        return WebClient.builder()
                .baseUrl("https://vision.googleapis.com/v1/images:annotate")
                .defaultHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }
}
