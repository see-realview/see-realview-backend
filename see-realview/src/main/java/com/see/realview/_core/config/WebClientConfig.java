package com.see.realview._core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.search.naver.id}")
    private String CLIENT_ID;

    @Value("${api.search.naver.secret}")
    private String CLIENT_SECRET;

    @Value("${api.search.naver.url}")
    private String REQUEST_URL;

    private final static String GOOGLE_VISION_API_URL = "https://vision.googleapis.com/v1/images:annotate";
    private final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";


    @Bean(name = "naverWebClient")
    public WebClient naverWebClient() {
        return WebClient.builder()
                .baseUrl(REQUEST_URL)
                .defaultHeader("X-Naver-Client-Id", CLIENT_ID)
                .defaultHeader("X-Naver-Client-Secret", CLIENT_SECRET)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .build();
    }

    @Bean(name = "googleWebClient")
    public WebClient googleWebClient() {
        return WebClient.builder()
                .baseUrl(GOOGLE_VISION_API_URL)
                .defaultHeader("Content-Type", "application/json; charset=utf-8")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .build();
    }

    @Bean(name = "imageWebClient")
    public WebClient imageWebClient() {
        return WebClient.builder()
                .defaultHeader("User-Agent", USER_AGENT)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .build();
    }
}
