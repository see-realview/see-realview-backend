package com.see.realview.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.see.realview.analyzer.dto.request.ImageParseRequest;
import com.see.realview.analyzer.dto.response.PostDTO;
import com.see.realview.analyzer.service.RequestConverter;
import com.see.realview.analyzer.service.TextParser;
import com.see.realview.google.dto.RequestFeature;
import com.see.realview.google.dto.RequestItem;
import com.see.realview.google.dto.RequestIterator;
import com.see.realview.google.dto.VisionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class GoogleVisionAPI {

    private final RequestConverter requestConverter;

    private final WebClient googleWebClient;

    private final TextParser textParser;

    private final ObjectMapper objectMapper;

    @Value("${api.google.key}")
    private String GCP_KEY;

    private final static List<RequestFeature> requestFeatures = List.of(new RequestFeature("TEXT_DETECTION"));


    public GoogleVisionAPI(@Autowired @Qualifier("googleWebClient") WebClient googleWebClient,
                           @Autowired RequestConverter requestConverter,
                           @Autowired TextParser textParser,
                           @Autowired ObjectMapper objectMapper) {
        this.googleWebClient = googleWebClient;
        this.requestConverter = requestConverter;
        this.textParser = textParser;
        this.objectMapper = objectMapper;
    }

    public List<PostDTO> call(List<ImageParseRequest> requests) {
        List<RequestIterator> requestIterators = requestConverter.getRequestIterators(requests, requestFeatures);
        List<RequestItem> items = requestConverter.getRequestItems(requestIterators);

        List<String> responses = new ArrayList<>();

        try {
            StringBuilder result = getVisionAPIResponse(items);
            parseVisionAPIResponse(responses, result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Queue<String> resultQueue = new LinkedList<>(responses);

        return requests.stream()
                .map(parseRequest -> parse(resultQueue, parseRequest))
                .toList();
    }

    private StringBuilder getVisionAPIResponse(List<RequestItem> items) throws JsonProcessingException {
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

    private void parseVisionAPIResponse(List<String> responses, StringBuilder result) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(result.toString());
        JsonNode responsesNode = rootNode.path("responses");

        responsesNode.forEach(node -> {
            JsonNode textAnnotationsNode = node.path("textAnnotations");
            JsonNode firstTextAnnotation = textAnnotationsNode.get(0);
            if (firstTextAnnotation != null) {
                String description = firstTextAnnotation.path("description").asText();
                responses.add(description);
            }
        });
    }

    private PostDTO parse(Queue<String> resultQueue, ImageParseRequest parseRequest) {
        boolean advertisement = false;

        if (parseRequest.required()) {
            String imageText = resultQueue.poll();
            advertisement = textParser.analyzeImageText(imageText);
        }

        return PostDTO.of(parseRequest, advertisement, 0L);
    }
}
