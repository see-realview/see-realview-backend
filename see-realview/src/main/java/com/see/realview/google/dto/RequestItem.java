package com.see.realview.google.dto;

import java.util.List;

public record RequestItem(
        RequestImage image,
        List<RequestFeature> features
) {
}
