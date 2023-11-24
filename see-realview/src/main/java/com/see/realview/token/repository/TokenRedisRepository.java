package com.see.realview.token.repository;

import com.see.realview.token.entity.Token;

import java.util.Optional;

public interface TokenRedisRepository {

    Optional<Token> findTokenById(Long id);

    void deleteById(Long id);
}
