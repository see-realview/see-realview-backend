package com.see.realview.token.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.token.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TokenRedisRepositoryImpl implements TokenRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final ValueOperations<String, String> valueOperations;

    private final ObjectMapper objectMapper;

    private final static String TOKEN_PREFIX = "token_";

    public TokenRedisRepositoryImpl(@Autowired RedisTemplate<String, String> redisTemplate,
                                    @Autowired ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Token> findTokenById(Long id) {
        String key = getKeyById(id);
        String value = valueOperations.get(key);

        if (value == null) {
            return Optional.empty();
        }

        Token token = getToken(value);
        return Optional.ofNullable(token);
    }

    @Override
    public void save(Long id, Token token) {
        String key = getKeyById(id);
        String value = getValue(token);
        valueOperations.set(key, value);
    }

    @Override
    public void deleteById(Long id) {
        String key = getKeyById(id);
        redisTemplate.delete(key);
    }

    @Override
    public boolean isTokenExists(Long id) {
        String key = getKeyById(id);
        return valueOperations.get(key) != null;
    }

    private static String getKeyById(Long id) {
        return TOKEN_PREFIX + id;
    }

    private String getValue(Token token) {
        try {
            return objectMapper.writeValueAsString(token);
        }
        catch (JsonProcessingException exception) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }

    private Token getToken(String value) {
        try {
            return objectMapper.readValue(value, Token.class);
        }
        catch (JsonProcessingException exception) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }
}
