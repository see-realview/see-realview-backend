package com.see.realview.user.service;

import com.see.realview.token.entity.Token;
import com.see.realview.user.dto.request.LoginRequest;
import com.see.realview.user.dto.request.RegisterRequest;
import com.see.realview.user.entity.UserAccount;

import java.util.Optional;

public interface UserService {

    void register(RegisterRequest request);

    Token login(LoginRequest request);

    Optional<UserAccount> findByEmail(String email);
}
