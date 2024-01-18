package com.see.realview.image.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.image.dto.CachedImage;
import com.see.realview.image.dto.ImageData;
import com.see.realview.image.entity.ParsedImage;
import com.see.realview.image.repository.ParsedImageRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class ParsedImageRedisRepositoryImpl implements ParsedImageRedisRepository {
    
    private final RedisTemplate redisTemplate;
    
    private final ValueOperations<String, String> valueOperations;

    private final ObjectMapper objectMapper;

    private final static String IMAGE_PREFIX = "image_";

    @Value("${api.image.expire}")
    private Long CACHE_EXPIRE;


    public ParsedImageRedisRepositoryImpl(@Autowired RedisTemplate<String, String> redisTemplate,
                                          @Autowired ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<CachedImage> findByURL(String url) {
        String key = getKeyByURL(url);
        String data = valueOperations.get(key);

        if (data == null) {
            return Optional.empty();
        }

        ImageData imageData = getImageData(data);
        CachedImage cachedImage = new CachedImage(key, imageData);
        return Optional.of(cachedImage);
    }

    @Override
    public List<CachedImage> findAll() {
        List<CachedImage> images = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions().match(IMAGE_PREFIX + "*").build();
        Cursor<String> cursor = redisTemplate.scan(options);

        while (cursor.hasNext()) {
            String key = cursor.next();
            String data = valueOperations.get(key);

            String url = key.replace(IMAGE_PREFIX, "");
            if (url.equals("")) {
                log.debug("Redis 데이터 empty 오류 발생 | " + key);
                continue;
            }

            ImageData imageData = getImageData(data);

            images.add(
                    new CachedImage(url, imageData)
            );
        }

        return images;
    }

    @Override
    public void save(CachedImage image) {
        String key = getKeyByURL(image.link());
        String value = getValue(image);

        valueOperations.set(key, value);
        redisTemplate.expire(key, CACHE_EXPIRE, TimeUnit.MINUTES);
    }

    @Override
    public void saveAll(List<ParsedImage> images) {
        images
                .forEach(image -> {
                    ImageData data = new ImageData(image.getAdvertisement(), image.getCount());
                    CachedImage cachedImage = new CachedImage(image.getLink(), data);
                    save(cachedImage);
                });
    }

    @Override
    public void deleteAll() {
        ScanOptions options = ScanOptions.scanOptions().match(IMAGE_PREFIX).build();
        Cursor<String> cursor = redisTemplate.scan(options);

        while (cursor.hasNext()) {
            String next = cursor.next();
            redisTemplate.delete(next);
        }
    }

    private String getKeyByURL(String url) {
        if (url.startsWith(IMAGE_PREFIX)) {
            return url;
        }

        return IMAGE_PREFIX + url;
    }

    private String getValue(CachedImage image) {
        try {
            return objectMapper.writeValueAsString(image.data());
        }
        catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }

    private ImageData getImageData(String data) {
        try {
            return objectMapper.readValue(data, ImageData.class);
        }
        catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }
}
