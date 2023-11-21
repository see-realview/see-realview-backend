package com.see.realview._core.config;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.ForbiddenException;
import com.see.realview._core.exception.client.UnauthorizedException;
import com.see.realview._core.security.CorsConfig;
import com.see.realview._core.security.CustomSecurityFilterManager;
import com.see.realview._core.security.JwtProvider;
import com.see.realview._core.utils.ExceptionResponseWriter;
import com.see.realview.token.service.TokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    private final TokenServiceImpl tokenService;

    private final ExceptionResponseWriter responseWriter;


    public SecurityConfig(@Autowired JwtProvider jwtProvider,
                          @Autowired TokenServiceImpl tokenService,
                          @Autowired ExceptionResponseWriter responseWriter) {
        this.jwtProvider = jwtProvider;
        this.tokenService = tokenService;
        this.responseWriter = responseWriter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        http.csrf(CsrfConfigurer::disable);

        http.cors((cors) -> cors.configurationSource(CorsConfig.getConfigurationSource()));

        http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(FormLoginConfigurer::disable);

        http.httpBasic(HttpBasicConfigurer::disable);

        http.apply(new CustomSecurityFilterManager(jwtProvider, tokenService));

        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                    responseWriter.write(response, new UnauthorizedException(ExceptionStatus.UNAUTHORIZED));
                })
        );

        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling.accessDeniedHandler((request, response, accessDeniedException) -> {
                    responseWriter.write(response, new ForbiddenException(ExceptionStatus.FORBIDDEN));
                })
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(
                                new AntPathRequestMatcher("/user/register"),
                                new AntPathRequestMatcher("/user/login"),
                                new AntPathRequestMatcher("/search/**")
                        ).permitAll()
                        .anyRequest().authenticated()
        );

        return http.build();
    }
}
