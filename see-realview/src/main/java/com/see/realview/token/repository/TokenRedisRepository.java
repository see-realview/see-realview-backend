package com.see.realview.token.repository;

import com.see.realview.token.entity.Token;

import java.util.Optional;

public interface TokenRedisRepository {

    Optional<Token> findTokenById(Long id);

    void save(Long id, Token token);

    void deleteById(Long id);

    boolean isTokenExists(Long id);
}
