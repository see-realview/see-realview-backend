package com.see.realview.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.analyzer.dto.request.ImageParseRequest;
import com.see.realview.analyzer.dto.response.PostDTO;
import com.see.realview.analyzer.service.RequestConverter;
import com.see.realview.analyzer.service.TextParser;
import com.see.realview.google.dto.RequestFeature;
import com.see.realview.google.dto.RequestItem;
import com.see.realview.google.dto.RequestIterator;
import com.see.realview.google.dto.VisionRequest;
import com.see.realview.image.entity.ParsedImage;
import com.see.realview.image.service.ParsedImageService;
import com.see.realview.image.service.ParsedImageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@Slf4j
public class GoogleVisionAPI {

    private final ParsedImageService parsedImageService;

    private final RequestConverter requestConverter;

    private final WebClient googleWebClient;

    private final TextParser textParser;

    private final ObjectMapper objectMapper;

    @Value("${api.google.key}")
    private String GCP_KEY;

    private final static List<RequestFeature> requestFeatures = List.of(new RequestFeature("TEXT_DETECTION"));


    public GoogleVisionAPI(@Autowired ParsedImageService parsedImageService,
                           @Autowired @Qualifier("googleWebClient") WebClient googleWebClient,
                           @Autowired RequestConverter requestConverter,
                           @Autowired TextParser textParser,
                           @Autowired ObjectMapper objectMapper) {
        this.parsedImageService = parsedImageService;
        this.googleWebClient = googleWebClient;
        this.requestConverter = requestConverter;
        this.textParser = textParser;
        this.objectMapper = objectMapper;
    }

    public List<PostDTO> call(List<ImageParseRequest> requests) {
        List<RequestIterator> requestIterators = requestConverter.getRequestIterators(requests, requestFeatures);
        List<RequestItem> items = requestConverter.getRequestItems(requestIterators);

        List<String> responses;

        StringBuilder result = getVisionAPIResponse(items);
        responses = parseVisionAPIResponse(result);

        Collections.reverse(responses);
        Queue<String> resultQueue = new LinkedList<>(responses);

        List<ParsedImage> images = new ArrayList<>();

        List<PostDTO> posts =  requests.stream()
                .map(parseRequest -> {
                    boolean advertisement = getImageParseResponse(resultQueue, parseRequest);
                    String url = parseRequest.url();

                    if (parseRequest.required()) {
                        String rawURL = url.replaceAll("\\?.*$", "");
                        images.add(ParsedImage.of(rawURL, advertisement));
                    }
                    else {
                        advertisement = parseRequest.advertisement();
                        if (url != null) {
                            images.add(ParsedImage.of(url, advertisement));
                        }
                    }

                    log.debug("OCR 진행됨 | " + parseRequest.request().link());
                    return PostDTO.of(parseRequest, advertisement, 0L);
                })
                .toList();

        parsedImageService.saveAll(images);

        return posts;
    }

    private StringBuilder getVisionAPIResponse(List<RequestItem> items) {
        try {
            String body = objectMapper.writeValueAsString(new VisionRequest(items));
            StringBuilder result = new StringBuilder();

            googleWebClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", GCP_KEY).build())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .toStream()
                    .forEach(result::append);

            return result;
        }
        catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }

    private List<String> parseVisionAPIResponse(StringBuilder result) {
        try {
            List<String> responses = new ArrayList<>();
            JsonNode rootNode = objectMapper.readTree(result.toString());
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

            return responses;
        }
        catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }

    private boolean getImageParseResponse(Queue<String> resultQueue, ImageParseRequest parseRequest) {
        if (parseRequest.required()) {
            String imageText = resultQueue.poll();
            System.out.println(parseRequest.url() + " | " + imageText);
            return textParser.analyzeImageText(imageText);
        }

        return false;
    }
}
