package com.see.realview._core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class WebDatabaseReader {

    private final WebClient databaseWebClient;

    public WebDatabaseReader(@Autowired @Qualifier("databaseWebClient") WebClient databaseWebClient) {
        this.databaseWebClient = databaseWebClient;
    }

    public byte[] read(String url) {
        log.debug("웹 데이터베이스 다운로드 시작 | " + url);
        Mono<byte[]> readBytes = databaseWebClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(byte[].class);

        byte[] dataBytes = readBytes.block();
        log.debug("웹 데이터베이스 다운로드 완료 | " + url);
        return dataBytes;
    }
}
