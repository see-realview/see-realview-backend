package com.see.realview.analyzer.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.analyzer.dto.request.ImageParseRequest;
import com.see.realview.analyzer.dto.response.PostDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class GoogleVisionOCR {

    public List<PostDTO> parse(List<ImageParseRequest> requests, int count) {
        if (count == 0) {

        }

        List<AnnotateImageRequest> imageRequests = createAnnotateImageRequests(requests);

        List<PostDTO> data = new ArrayList<>();
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(imageRequests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            List<String> result = parseOCRResponse(responses);

            if (result.isEmpty()) return null;
            Queue<String> resultQueue = new LinkedList<>(result);

            data = requests.stream()
                    .map(parseRequest -> {
                        boolean advertisement = false;

                        if (parseRequest.required()) {
                            String imageText = resultQueue.poll();
                            advertisement = (imageText != null) && (imageText.trim().contains("지원받") || imageText.trim().contains("제공받"));
                        }

                        return new PostDTO(
                                parseRequest.request().link(),
                                parseRequest.request().title(),
                                parseRequest.request().description(),
                                parseRequest.request().date(),
                                parseRequest.request().bloggerName(),
                                advertisement,
                                0L
                        );
                    })
                    .toList();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return data;
    }

    private static List<AnnotateImageRequest> createAnnotateImageRequests(List<ImageParseRequest> requests) {
        List<AnnotateImageRequest> imageRequests = new ArrayList<>();
        requests.stream()
                .parallel()
                .forEach(parseRequest -> {
                    if (parseRequest.required()) {
                        imageRequests.add(createOCRRequest(parseRequest.url()));
                    }
                });
        return imageRequests;
    }

    private static AnnotateImageRequest createOCRRequest(String url) {
        try {
            URL imageURL = new URL(url);
            byte[] imageBytes = imageURL.openConnection().getInputStream().readAllBytes();
            ByteString bytes = ByteString.copyFrom(imageBytes);

            Image img = Image.newBuilder().setContent(bytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            return AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        }
        catch (IOException exception) {
            throw new ServerException(ExceptionStatus.IMAGE_PARSING_ERROR);
        }
    }

    private static List<String> parseOCRResponse(List<AnnotateImageResponse> responses) {
        return responses
                .stream()
                .map(annotateImageResponse -> {
                    if (annotateImageResponse.hasError()) {
                        return annotateImageResponse.getError().getMessage();
                    }

                    List<EntityAnnotation> entityAnnotations = annotateImageResponse.getTextAnnotationsList();
                    if (entityAnnotations.size() != 0) {
                        return entityAnnotations.get(0).getDescription();
                    }
                    else return "";
                })
                .toList();
    }
}
