package com.see.realview.user.service;

import com.see.realview.token.entity.TokenPair;
import com.see.realview.user.dto.request.LoginRequest;
import com.see.realview.user.dto.request.RegisterRequest;

public interface UserService {
    void register(RegisterRequest request);
    TokenPair login(LoginRequest request);
}
