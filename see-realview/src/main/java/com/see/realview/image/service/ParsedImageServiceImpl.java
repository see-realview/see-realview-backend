package com.see.realview.image.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.NotFoundException;
import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.dto.ImageData;
import com.see.realview.image.entity.ParsedImage;
import com.see.realview.image.repository.ParsedImageJPARepository;
import com.see.realview.image.repository.ParsedImageRedisRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ParsedImageServiceImpl implements ParsedImageService {

    private final ParsedImageJPARepository parsedImageJPARepository;

    private final ParsedImageRedisRepositoryImpl parsedImageRedisRepository;


    public ParsedImageServiceImpl(@Autowired ParsedImageJPARepository parsedImageJPARepository,
                                  @Autowired ParsedImageRedisRepositoryImpl parsedImageRedisRepository) {
        this.parsedImageJPARepository = parsedImageJPARepository;
        this.parsedImageRedisRepository = parsedImageRedisRepository;
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
    @Transactional
    public void rebase() {
        List<CachedImage> cachedImages = parsedImageRedisRepository.findAll();
        List<String> urls = cachedImages.stream().map(CachedImage::url).toList();
        List<ParsedImage> parsedImages = parsedImageJPARepository.findAllByUrlIn(urls);

        cachedImages
                .forEach(image -> {
                    ParsedImage saved = findParsedImageInJPA(parsedImages, image);
                    saved.updateCount(image.data().count());
                });

        parsedImageJPARepository.saveAll(parsedImages);
        parsedImageRedisRepository.deleteAll();

        parsedImageJPARepository
                .findTop30ByOrderByCountDesc()
                .forEach(image -> {
                    ImageData imageData = new ImageData(image.getAdvertisement(), image.getCount());
                    CachedImage cachedImage = new CachedImage(image.getUrl(), imageData);

                    parsedImageRedisRepository.save(cachedImage);
                });
    }

    private static ParsedImage findParsedImageInJPA(List<ParsedImage> parsedImages, CachedImage image) {
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
