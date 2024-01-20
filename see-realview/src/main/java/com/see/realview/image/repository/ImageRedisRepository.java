package com.see.realview.image.repository;

import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.entity.Image;

import java.util.List;
import java.util.Optional;

public interface ImageRedisRepository {

    Optional<CachedImage> findByURL(String key);

    List<CachedImage> findAll();

    void save(CachedImage image);

    void saveAll(List<Image> images);

    void deleteAll();
}
