package com.see.realview.google.dto;

import java.util.List;

public record VisionRequest(
        List<RequestItem> requests
) {
}
