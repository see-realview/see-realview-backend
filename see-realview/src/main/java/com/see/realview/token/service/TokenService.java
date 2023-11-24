package com.see.realview.token.service;

import com.see.realview.token.entity.Token;
import com.see.realview.user.entity.UserAccount;

public interface TokenService {

    Token findTokenById(Long id);

    void save(Long id, Token token);

    Token refresh(UserAccount userAccount);

    void deleteById(Long id);
}
