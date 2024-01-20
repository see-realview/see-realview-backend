package com.see.realview.user.controller;

import com.see.realview._core.response.Response;
import com.see.realview._core.security.JwtProvider;
import com.see.realview.token.entity.Token;
import com.see.realview.token.entity.constants.Header;
import com.see.realview.user.dto.request.LoginRequest;
import com.see.realview.user.dto.request.RegisterRequest;
import com.see.realview.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok().body(Response.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Token token = userService.login(request);
        return ResponseEntity
                .ok()
                .header(Header.AUTHORIZATION.value(), JwtProvider.TOKEN_PREFIX + token.accessToken())
                .header(Header.REFRESH.value(), JwtProvider.TOKEN_PREFIX + token.refreshToken())
                .body(Response.of());
    }
}
