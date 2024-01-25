package com.see.realview.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.google.dto.RequestFeature;
import com.see.realview.google.dto.RequestItem;
import com.see.realview.google.dto.RequestIterator;
import com.see.realview.google.dto.VisionRequest;
import com.see.realview.search.dto.request.ImageParseRequest;
import com.see.realview.search.service.RequestConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GoogleVisionAPI {

    private final RequestConverter requestConverter;

    private final WebClient googleWebClient;

    private final ObjectMapper objectMapper;

    @Value("${api.google.key}")
    private String GCP_KEY;

    private final static List<RequestFeature> features = List.of(new RequestFeature("TEXT_DETECTION"));


    public GoogleVisionAPI(@Autowired @Qualifier("googleWebClient") WebClient googleWebClient,
                           @Autowired RequestConverter requestConverter,
                           @Autowired ObjectMapper objectMapper) {
        this.googleWebClient = googleWebClient;
        this.requestConverter = requestConverter;
        this.objectMapper = objectMapper;
    }

    public List<String> call(List<ImageParseRequest> requests) {
        if (requests.isEmpty()) {
            log.debug("Vision API 요청 내용 없음.");
            return List.of();
        }

        log.debug("Vision API 요청 생성 시작");
        List<RequestIterator> requestIterators = requestConverter.getRequestIterators(requests, features);
        List<RequestItem> items = requestConverter.getRequestItems(requestIterators);
        log.debug("Vision API 요청 생성 완료. API 호출");
        Mono<String> resultMono = getVisionAPIResponse(items);
        String result = resultMono.block();
        log.debug("Vision API 응답 수신 완료");
        return parseVisionAPIResponse(result);
    }

    private Mono<String> getVisionAPIResponse(List<RequestItem> items) {
        try {
            String body = objectMapper.writeValueAsString(new VisionRequest(items));
            return googleWebClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", GCP_KEY).build())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class);
        }
        catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }

    private List<String> parseVisionAPIResponse(String result) {
        try {
            log.debug("Vision API 응답 결과 파싱");
            List<String> responses = new ArrayList<>();
            JsonNode rootNode = objectMapper.readTree(result);
            JsonNode responsesNode = rootNode.path("responses");

            responsesNode.forEach(node -> {
                JsonNode textAnnotationsNode = node.path("textAnnotations");
                JsonNode firstTextAnnotation = textAnnotationsNode.get(0);
                if (firstTextAnnotation != null) {
                    String description = firstTextAnnotation.path("description").asText().replaceAll("\\n", " ");
                    responses.add(description);
                }
                else responses.add("");
            });

            log.debug("Vision API 응답 결과 파싱 완료");
            return responses;
        }
        catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }
}
