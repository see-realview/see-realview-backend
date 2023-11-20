package com.see.realview.token.service;

import com.see.realview.core.exception.ExceptionStatus;
import com.see.realview.core.exception.NotFoundException;
import com.see.realview.token.entity.Token;
import com.see.realview.token.repository.TokenRedisRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRedisRepositoryImpl tokenRedisRepository;


    public TokenServiceImpl(@Autowired TokenRedisRepositoryImpl tokenRedisRepository) {
        this.tokenRedisRepository = tokenRedisRepository;
    }

    @Override
    public Token findTokenById(Long id) {
        return tokenRedisRepository.findTokenById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionStatus.TOKEN_NOT_FOUND));
    }

    @Override
    public void deleteById(Long id) {
        tokenRedisRepository.deleteById(id);
    }
}
