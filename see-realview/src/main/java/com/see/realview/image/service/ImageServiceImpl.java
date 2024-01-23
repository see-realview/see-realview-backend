package com.see.realview.image.service;

import com.see.realview._core.utils.WebDatabaseReader;
import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.dto.ImageData;
import com.see.realview.image.entity.Image;
import com.see.realview.image.repository.ImageRedisRepository;
import com.see.realview.image.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    private final ImageRedisRepository imageRedisRepository;

    private final WebDatabaseReader webDatabaseReader;

    private final static String WEB_DATABASE_URL = "https://raw.githubusercontent.com/seokwns/see-realview-backend/develop/web-db/well-known-urls.txt";

    private List<String> WELL_KNOWN_URLS = new ArrayList<>();


    public ImageServiceImpl(@Autowired ImageRepository imageRepository,
                            @Autowired ImageRedisRepository imageRedisRepository,
                            @Autowired WebDatabaseReader webDatabaseReader) {
        this.imageRepository = imageRepository;
        this.imageRedisRepository = imageRedisRepository;
        this.webDatabaseReader = webDatabaseReader;
    }

    @Override
    public Optional<CachedImage> isAlreadyParsedImage(String url) {
        Optional<CachedImage> cachedImage = imageRedisRepository.findByURL(url);

        if (cachedImage.isEmpty()) {
            return Optional.empty();
        }

        CachedImage newImage = cachedImage.get().increment();
        imageRedisRepository.save(newImage);

        return Optional.of(newImage);
    }

    @Override
    public void save(String url, Boolean advertisement) {
        ImageData imageData = new ImageData(advertisement, 0L);
        CachedImage cachedImage = new CachedImage(url, imageData);
        imageRedisRepository.save(cachedImage);
    }

    @Override
    public void saveAll(List<Image> images) {
        if (images.isEmpty()) {
            return;
        }

        imageRedisRepository.saveAll(images);
    }

    @Override
    @Transactional
    public void rebase() {
        log.debug("이미지 캐싱 데이터 rebase 시작");
        List<CachedImage> cachedImages = imageRedisRepository.findAll();
        log.debug("Redis 데이터 조회 완료. 데이터베이스 조회 시작");
        List<String> urls = cachedImages.stream().map(CachedImage::link).toList();
        List<Image> images = imageRepository.findAllByUrlIn(urls);

        log.debug("데이터베이스 조회 완료. Redis -> DB 이동 시작");
        cachedImages
                .forEach(image -> {
                    Image saved = findParsedImage(images, image);
                    saved.updateCount(image.data().count());
                });
        imageRepository.saveAll(images);

        log.debug("Redis 데이터 이동 완료. Redis 데이터 삭제 시작.");
        imageRedisRepository.deleteAll();

        log.debug("Redis 데이터 삭제 완료. DB -> Redis 이동 시작");
        imageRepository
                .findCachingImages()
                .forEach(image -> {
                    ImageData imageData = new ImageData(image.getAdvertisement(), 0L);
                    CachedImage cachedImage = new CachedImage(image.getLink(), imageData);

                    imageRedisRepository.save(cachedImage);
                });

        log.debug("DB 데이터 이동 완료. rebase 완료");
    }

    private static Image findParsedImage(List<Image> images, CachedImage image) {
        return images
                .stream()
                .filter(img -> img.getLink().equals(image.link()))
                .findFirst()
                .orElseGet(() -> {
                    Image newImage = Image.builder()
                            .link(image.link())
                            .advertisement(image.data().advertisement())
                            .build();

                    images.add(newImage);
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
