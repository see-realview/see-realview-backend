package com.see.realview.image.repository;

import com.see.realview.image.entity.Image;

import java.util.List;

public interface ImageRepository {

    List<Image> findAllByUrlIn(List<String> urls);

    List<Image> findCachingImages();

    void save(Image image);

    void saveAll(List<Image> images);
}
