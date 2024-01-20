package com.see.realview.image.service;

import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.entity.Image;

import java.util.List;
import java.util.Optional;

public interface ImageService {

    Optional<CachedImage> isAlreadyParsedImage(String url);

    void save(String url, Boolean advertisement);

    void saveAll(List<Image> images);

    void rebase();

    void rebaseWebDatabase();

    Boolean isWellKnownURL(String url);
}
