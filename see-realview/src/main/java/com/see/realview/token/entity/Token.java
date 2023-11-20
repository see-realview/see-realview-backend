package com.see.realview.token.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "token")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    private Long userAccountId;

    private TokenPair tokenPair;

    public Boolean equals(String accessToken, String refreshToken) {
        return tokenPair.access().equals(accessToken) && tokenPair.refresh().equals(refreshToken);
    }
}
