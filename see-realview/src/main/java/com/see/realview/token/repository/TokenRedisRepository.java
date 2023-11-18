package com.see.realview.token.repository;

import com.see.realview.token.entity.Token;

public interface TokenRedisRepository {

    Token findTokenByEmail(String email);

}
