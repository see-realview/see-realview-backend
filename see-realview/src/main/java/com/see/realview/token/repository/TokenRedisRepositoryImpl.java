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
        String token = valueOperations.get(key);

        if (token == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(objectMapper.readValue(token, Token.class));
        }
        catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.TOKEN_PARSING_ERROR);
        }
    }

    @Override
    public void save(Long id, Token token) {
        try {
            String key = getKeyById(id);
            String value = objectMapper.writeValueAsString(token);
            valueOperations.set(key, value);
        }
        catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.TOKEN_PARSING_ERROR);
        }
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
}
