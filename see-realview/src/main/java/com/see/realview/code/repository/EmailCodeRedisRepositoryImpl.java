package com.see.realview.code.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EmailCodeRedisRepositoryImpl implements EmailCodeRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final ValueOperations<String, String> valueOperations;

    private final static String EMAIL_PREFIX = "email_";


    public EmailCodeRedisRepositoryImpl(@Autowired RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void save(String email, String code) {
        String key = getKey(email);
        valueOperations.set(key, code);
    }

    @Override
    public Optional<String> findCodeByEmail(String email) {
        String key = getKey(email);
        String value = valueOperations.get(key);

        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(value);
    }

    private String getKey(String email) {
        return EMAIL_PREFIX + email;
    }
}
