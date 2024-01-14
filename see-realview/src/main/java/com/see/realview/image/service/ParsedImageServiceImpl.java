package com.see.realview.image.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.NotFoundException;
import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.dto.ImageData;
import com.see.realview.image.entity.ParsedImage;
import com.see.realview.image.repository.ParsedImageRedisRepository;
import com.see.realview.image.repository.ParsedImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
    public Boolean isWellKnownURL(String url) {
        return parsedImageRepository.isWellKnownURL(url);
    }

    @Override
    public Optional<ParsedImage> findByURL(String link) {
        Optional<CachedImage> cachedImage = parsedImageRedisRepository.findByURL(link);

        if (cachedImage.isEmpty()) {
            return Optional.empty();
        }

        CachedImage image = cachedImage.get();
        ParsedImage parsedImage = ParsedImage.builder()
                .link(link)
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
        if (images.isEmpty()) {
            return;
        }

        parsedImageRedisRepository.saveAll(images);
    }

    @Override
    @Transactional
    public void rebase() {
        log.debug("이미지 캐싱 데이터 rebase 시작");
        List<CachedImage> cachedImages = parsedImageRedisRepository.findAll();
        List<String> urls = cachedImages.stream().map(CachedImage::link).toList();
        List<ParsedImage> parsedImages = parsedImageRepository.findAllByUrlIn(urls);

        log.debug("데이터 조회 완료. redis 데이터 -> DB 이동 시작");
        cachedImages
                .forEach(image -> {
                    ParsedImage saved = findParsedImage(parsedImages, image);
                    saved.updateCount(image.data().count());
                });

        parsedImageRepository.saveAll(parsedImages);

        log.debug("redis 데이터 저장 완료. 데이터 캐싱 시작");
        parsedImageRepository
                .findCachingImages()
                .forEach(image -> {
                    ImageData imageData = new ImageData(image.getAdvertisement(), image.getCount());
                    CachedImage cachedImage = new CachedImage(image.getLink(), imageData);

                    parsedImageRedisRepository.save(cachedImage);
                });

        log.debug("이미지 캐싱 데이터 rebase 완료");
    }

    private static ParsedImage findParsedImage(List<ParsedImage> parsedImages, CachedImage image) {
        return parsedImages
                .stream()
                .filter(img -> img.getLink().equals(image.link()))
                .findFirst()
                .orElseGet(() -> {
                    ParsedImage newImage = ParsedImage.builder()
                            .link(image.link())
                            .advertisement(image.data().advertisement())
                            .build();

                    parsedImages.add(newImage);
                    return newImage;
                });
    }
}
