package com.see.realview.token.repository;

import com.see.realview.token.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TokenRedisRepositoryImpl implements TokenRedisRepository {

    private final RedisTemplate<Long, Token> redisTemplate;

    private final ValueOperations<Long, Token> valueOperations;

    public TokenRedisRepositoryImpl(@Autowired RedisTemplate<Long, Token> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public Optional<Token> findTokenById(Long id) {
        Token token = valueOperations.get(id);
        return Optional.ofNullable(token);
    }

    @Override
    public void deleteById(Long id) {
        redisTemplate.delete(id);
    }
}
