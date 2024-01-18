package com.see.realview.image.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.NotFoundException;
import com.see.realview._core.utils.WebDatabaseReader;
import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.dto.ImageData;
import com.see.realview.image.entity.ParsedImage;
import com.see.realview.image.repository.ParsedImageRedisRepository;
import com.see.realview.image.repository.ParsedImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ParsedImageServiceImpl implements ParsedImageService {

    private final ParsedImageRepository parsedImageRepository;

    private final ParsedImageRedisRepository parsedImageRedisRepository;

    private final WebDatabaseReader webDatabaseReader;

    private final static String WEB_DATABASE_URL = "https://raw.githubusercontent.com/seokwns/see-realview-backend/develop/web-db/well-known-urls.txt";

    private List<String> WELL_KNOWN_URLS = new ArrayList<>();


    public ParsedImageServiceImpl(@Autowired ParsedImageRepository parsedImageRepository,
                                  @Autowired ParsedImageRedisRepository parsedImageRedisRepository,
                                  @Autowired WebDatabaseReader webDatabaseReader) {
        this.parsedImageRepository = parsedImageRepository;
        this.parsedImageRedisRepository = parsedImageRedisRepository;
        this.webDatabaseReader = webDatabaseReader;
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
        log.debug("Redis 데이터 조회 완료. 데이터베이스 조회 시작");
        List<String> urls = cachedImages.stream().map(CachedImage::link).toList();
        List<ParsedImage> parsedImages = parsedImageRepository.findAllByUrlIn(urls);

        log.debug("데이터베이스 조회 완료. Redis -> DB 이동 시작");
        cachedImages
                .forEach(image -> {
                    ParsedImage saved = findParsedImage(parsedImages, image);
                    saved.updateCount(image.data().count());
                });
        parsedImageRepository.saveAll(parsedImages);

        log.debug("Redis 데이터 이동 완료. Redis 데이터 삭제 시작.");
        parsedImageRedisRepository.deleteAll();

        log.debug("Redis 데이터 삭제 완료. DB -> Redis 이동 시작");
        parsedImageRepository
                .findCachingImages()
                .forEach(image -> {
                    ImageData imageData = new ImageData(image.getAdvertisement(), image.getCount());
                    CachedImage cachedImage = new CachedImage(image.getLink(), imageData);

                    parsedImageRedisRepository.save(cachedImage);
                });

        log.debug("DB 데이터 이동 완료. rebase 완료");
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

    @Override
    public void rebaseWebDatabase() {
        log.debug("WELL-KNOWN URLS 데이터 리프레시 시작");
        byte[] downloadData = webDatabaseReader.read(WEB_DATABASE_URL);
        if (downloadData == null) {
            log.debug("웹 데이터베이스 다운로드 에러 | " + WEB_DATABASE_URL);
        }

        String data = new String(downloadData, StandardCharsets.UTF_8);
        this.WELL_KNOWN_URLS = List.of(data.split("\n"));

        log.debug("WELL-KNOWN URLS 데이터 " + this.WELL_KNOWN_URLS.size() + "개 리프레시 완료");
    }

    @Override
    public Boolean isWellKnownURL(String url) {
        for (String data : this.WELL_KNOWN_URLS) {
            if (url.contains(data)) {
                return true;
            }
        }

        return false;
    }
}
