package com.see.realview._core.security;

import com.see.realview._core.exception.client.BadRequestException;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.UnauthorizedException;
import com.see.realview.token.entity.Token;
import com.see.realview.token.service.TokenServiceImpl;
import com.see.realview.user.entity.UserAccount;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final JwtProvider jwtProvider;

    private final TokenServiceImpl tokenService;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JwtProvider jwtProvider,
                                   TokenServiceImpl tokenService) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.tokenService = tokenService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accessHeader = request.getHeader(JwtProvider.AUTHORIZATION_HEADER);
        String refreshHeader = request.getHeader(JwtProvider.REFRESH_HEADER);

        if (accessHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        Long userAccountId = jwtProvider.verifyAccessToken(accessHeader);

        if (request.getRequestURI().contains("/token/refresh")) {
            if (refreshHeader == null) {
                throw new UnauthorizedException(ExceptionStatus.REFRESH_TOKEN_REQUIRED);
            }

            Token token = tokenService.findTokenById(userAccountId);
            if (!token.equals(accessHeader, refreshHeader)) {
                log.debug("토큰 쌍이 일치하지 않아 폐기합니다. id = " + userAccountId);
                throw new BadRequestException(ExceptionStatus.TOKEN_PAIR_NOT_MATCH);
            }

            tokenService.deleteById(userAccountId);
            createAuthentication(userAccountId);
            log.debug("리프래시 토큰을 이용한 토큰 생성. id = " + userAccountId);
            chain.doFilter(request, response);
        }
        else {
            createAuthentication(userAccountId);
            chain.doFilter(request, response);
        }

    }

    private void createAuthentication(Long userAccountId) {
        UserAccount userAccount = UserAccount.builder().id(userAccountId).build();
        CustomUserDetails userDetails = new CustomUserDetails(userAccount);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
