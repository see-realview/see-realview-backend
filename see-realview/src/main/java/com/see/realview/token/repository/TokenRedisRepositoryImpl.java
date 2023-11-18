package com.see.realview.token.repository;

import com.see.realview.core.exception.BaseException;
import com.see.realview.core.exception.NotFoundException;
import com.see.realview.token.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class TokenRedisRepositoryImpl implements TokenRedisRepository {

    private final RedisTemplate<String, Token> redisTemplate;

    private final ValueOperations<String, Token> valueOperations;

    public TokenRedisRepositoryImpl(@Autowired RedisTemplate<String, Token> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public Token findTokenByEmail(String email) {
        Token token = valueOperations.get(email);

        if (token == null) {
            throw new NotFoundException(BaseException.TOKEN_NOT_FOUND);
        }

        return token;
    }
}
