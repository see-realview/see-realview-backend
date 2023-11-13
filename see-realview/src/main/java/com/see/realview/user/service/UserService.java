package com.see.realview.user.service;

import com.see.realview.user.dto.request.LoginRequest;
import com.see.realview.user.dto.request.RegisterRequest;
import com.see.realview.user.dto.response.TokenPair;

public interface UserService {
    void register(RegisterRequest request);
    TokenPair login(LoginRequest request);
}
