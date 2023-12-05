package com.see.realview.image.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.NotFoundException;
import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.dto.ImageData;
import com.see.realview.image.entity.ParsedImage;
import com.see.realview.image.repository.ParsedImageRedisRepository;
import com.see.realview.image.repository.ParsedImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ParsedImageServiceImpl implements ParsedImageService {

    private final ParsedImageRepository parsedImageRepository;

    private final ParsedImageRedisRepository parsedImageRedisRepository;


    public ParsedImageServiceImpl(@Autowired ParsedImageRepository parsedImageRepository,
                                  @Autowired ParsedImageRedisRepository parsedImageRedisRepository) {
        this.parsedImageRepository = parsedImageRepository;
        this.parsedImageRedisRepository = parsedImageRedisRepository;
    }

    @Override
    public Optional<CachedImage> isAlreadyParsedImage(String url) {
        Optional<CachedImage> cachedImage = parsedImageRedisRepository.findByURL(url);

        if (cachedImage.isEmpty()) {
            return Optional.empty();
        }

        CachedImage newImage = cachedImage.get().increment();
        parsedImageRedisRepository.save(newImage);

        return Optional.of(newImage);
    }

    @Override
    public Optional<ParsedImage> findByURL(String url) {
        Optional<CachedImage> cachedImage = parsedImageRedisRepository.findByURL(url);

        if (cachedImage.isEmpty()) {
            return Optional.empty();
        }

        CachedImage image = cachedImage.get();
        ParsedImage parsedImage = ParsedImage.builder()
                .url(url)
                .advertisement(image.data().advertisement())
                .count(image.data().count())
                .build();

        return Optional.of(parsedImage);
    }

    @Override
    public void increment(String url) {
        CachedImage image = parsedImageRedisRepository.findByURL(url)
                .orElseThrow(() -> new NotFoundException(ExceptionStatus.CACHED_IMAGE_NOT_FOUND));

        CachedImage newImage = image.increment();
        parsedImageRedisRepository.save(newImage);
    }

    @Override
    public void save(String url, Boolean advertisement) {
        ImageData imageData = new ImageData(advertisement, 0L);
        CachedImage cachedImage = new CachedImage(url, imageData);
        parsedImageRedisRepository.save(cachedImage);
    }

    @Override
    public void saveAll(List<ParsedImage> images) {
        parsedImageRepository.saveAll(images);
    }

    @Override
    @Transactional
    public void rebase() {
        List<CachedImage> cachedImages = parsedImageRedisRepository.findAll();
        List<String> urls = cachedImages.stream().map(CachedImage::url).toList();
        List<ParsedImage> parsedImages = parsedImageRepository.findAllByUrlIn(urls);

        cachedImages
                .forEach(image -> {
                    ParsedImage saved = findParsedImage(parsedImages, image);
                    saved.updateCount(image.data().count());
                });

        parsedImageRepository.saveAll(parsedImages);
        parsedImageRedisRepository.deleteAll();

        parsedImageRepository
                .findCachingImages()
                .forEach(image -> {
                    ImageData imageData = new ImageData(image.getAdvertisement(), 0L);
                    CachedImage cachedImage = new CachedImage(image.getUrl(), imageData);

                    parsedImageRedisRepository.save(cachedImage);
                });
    }

    private static ParsedImage findParsedImage(List<ParsedImage> parsedImages, CachedImage image) {
        return parsedImages
                .stream()
                .filter(img -> img.getUrl().equals(image.url()))
                .findFirst()
                .orElseGet(() -> {
                    ParsedImage newImage = ParsedImage.builder()
                            .url(image.url())
                            .advertisement(image.data().advertisement())
                            .build();

                    parsedImages.add(newImage);
                    return newImage;
                });
    }
}
