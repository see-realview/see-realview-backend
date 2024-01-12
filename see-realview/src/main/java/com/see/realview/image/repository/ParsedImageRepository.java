package com.see.realview.image.repository;

import com.see.realview.image.entity.ParsedImage;

import java.util.List;

public interface ParsedImageRepository {

    List<ParsedImage> findAllByUrlIn(List<String> urls);

    List<ParsedImage> findCachingImages();

    void save(ParsedImage image);

    void saveAll(List<ParsedImage> images);

    Boolean isWellKnownURL(String url);
}
