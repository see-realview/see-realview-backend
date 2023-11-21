package com.see.realview._core.security;

import com.see.realview.domain.token.service.TokenServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {

    private final JwtProvider jwtProvider;

    private final TokenServiceImpl tokenService;


    public CustomSecurityFilterManager(JwtProvider jwtProvider,
                                       TokenServiceImpl tokenService) {
        this.jwtProvider = jwtProvider;
        this.tokenService = tokenService;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        builder.addFilter(new JwtAuthenticationFilter(authenticationManager, jwtProvider, tokenService));
        super.configure(builder);
    }
}
