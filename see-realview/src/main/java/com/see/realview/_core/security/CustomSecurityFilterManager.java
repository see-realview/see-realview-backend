package com.see.realview._core.security;

import com.see.realview._core.utils.ExceptionResponseWriter;
import com.see.realview.token.service.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {

    private final JwtProvider jwtProvider;

    private final TokenService tokenService;

    private final ExceptionResponseWriter responseWriter;


    public CustomSecurityFilterManager(JwtProvider jwtProvider,
                                       TokenService tokenService,
                                       ExceptionResponseWriter responseWriter) {
        this.jwtProvider = jwtProvider;
        this.tokenService = tokenService;
        this.responseWriter = responseWriter;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        builder.addFilter(new JwtAuthenticationFilter(authenticationManager, jwtProvider, tokenService));
        builder.addFilterBefore(new JwtExceptionFilter(responseWriter), JwtAuthenticationFilter.class);
        super.configure(builder);
    }
}
