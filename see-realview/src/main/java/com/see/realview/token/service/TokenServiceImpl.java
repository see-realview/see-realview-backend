package com.see.realview.token.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.NotFoundException;
import com.see.realview._core.security.JwtProvider;
import com.see.realview.token.entity.Token;
import com.see.realview.token.repository.TokenRedisRepository;
import com.see.realview.token.repository.TokenRedisRepositoryImpl;
import com.see.realview.user.entity.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRedisRepository tokenRedisRepository;

    private final JwtProvider jwtProvider;


    public TokenServiceImpl(@Autowired TokenRedisRepository tokenRedisRepository,
                            @Autowired JwtProvider jwtProvider) {
        this.tokenRedisRepository = tokenRedisRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Token findTokenById(Long id) {
        return tokenRedisRepository.findTokenById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionStatus.TOKEN_NOT_FOUND));
    }

    @Override
    public void save(Long id, Token token) {
        if (tokenRedisRepository.isTokenExists(id)) {
            deleteById(id);
        }

        tokenRedisRepository.save(id, token);
    }

    @Override
    public Token refresh(UserAccount userAccount) {
        String accessToken = jwtProvider.createAccessToken(userAccount);
        String refreshToken = jwtProvider.createRefreshToken(userAccount);

        Token token = new Token(accessToken, refreshToken);
        save(userAccount.getId(), token);

        return token;
    }

    @Override
    public void deleteById(Long id) {
        tokenRedisRepository.deleteById(id);
    }
}
