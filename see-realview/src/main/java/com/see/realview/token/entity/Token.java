package com.see.realview.token.entity;

import com.see.realview.token.dto.TokenPair;
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
    private String email;

    private TokenPair tokenPair;
}
