package com.see.realview._core.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.see.realview._core.exception.client.BadRequestException;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.UnauthorizedException;
import com.see.realview.token.entity.Token;
import com.see.realview.token.entity.constants.Header;
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
        String accessToken = resolveHeader(request, Header.AUTHORIZATION.value());
        String refreshToken = resolveHeader(request, Header.REFRESH.value());

        if (accessToken == null) {
            chain.doFilter(request, response);
            return;
        }

        DecodedJWT decodedJWT = jwtProvider.verifyAccessToken(accessToken);
        Long userAccountId = decodedJWT.getClaim("id").asLong();

        if (request.getRequestURI().contains("/token/refresh")) {
            if (refreshToken == null) {
                throw new UnauthorizedException(ExceptionStatus.REFRESH_TOKEN_REQUIRED);
            }

            if (jwtProvider.isValidAccessToken(accessToken)) {
                throw new UnauthorizedException(ExceptionStatus.ACCESS_TOKEN_NOT_EXPIRED);
            }

            Token token = tokenService.findTokenById(userAccountId);
            tokenService.deleteById(userAccountId);

            if (!token.equals(accessToken, refreshToken)) {
                throw new UnauthorizedException(ExceptionStatus.TOKEN_PAIR_NOT_MATCH);
            }
        }
        else {
            if (refreshToken != null) {
                throw new UnauthorizedException(ExceptionStatus.UNNECESSARY_REFRESH_TOKEN);
            }
        }

        setAuthentication(userAccountId);
        chain.doFilter(request, response);
    }

    private static String resolveHeader(HttpServletRequest request, String header) {
        String content = request.getHeader(header);

        if (content == null) {
            return null;
        }

        return content.substring(7);
    }

    private void setAuthentication(Long userAccountId) {
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
