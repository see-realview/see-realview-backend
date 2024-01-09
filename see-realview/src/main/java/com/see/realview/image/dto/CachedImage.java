package com.see.realview.image.dto;

public record CachedImage(
        String link,
        ImageData data
) {

    public CachedImage increment() {
        ImageData imageData = new ImageData(data.advertisement(), data.count() + 1);
        return new CachedImage(link, imageData);
    }
}
