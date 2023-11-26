package com.see.realview.image.service;

import com.see.realview.image.entity.ParsedImage;

import java.util.Optional;

public interface ParsedImageService {

    Optional<ParsedImage> findByURL(String url);

    void increment(String url);

    void save(String url, Boolean advertisement);

    void rebase();
}
