package com.see.realview.token.service;

import com.see.realview.token.entity.Token;

public interface TokenService {

    Token findTokenById(Long id);

    void deleteById(Long id);
}
