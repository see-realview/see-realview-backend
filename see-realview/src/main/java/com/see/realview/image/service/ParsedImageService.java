package com.see.realview.image.service;

import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.entity.ParsedImage;

import java.util.List;
import java.util.Optional;

public interface ParsedImageService {

    Optional<CachedImage> isAlreadyParsedImage(String url);

    Optional<ParsedImage> findByURL(String url);

    void increment(String url);

    void save(String url, Boolean advertisement);

    void saveAll(List<ParsedImage> images);

    void rebase();
}
