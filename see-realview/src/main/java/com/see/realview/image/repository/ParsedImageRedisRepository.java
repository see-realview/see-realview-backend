package com.see.realview.image.repository;

import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.entity.ParsedImage;

import java.util.List;
import java.util.Optional;

public interface ParsedImageRedisRepository {

    boolean isAlreadyParsed(String url);

    Optional<CachedImage> findByURL(String key);

    List<CachedImage> findAll();

    void save(CachedImage image);

    void deleteAll();
}
