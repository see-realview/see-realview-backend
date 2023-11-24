package com.see.realview._core.security;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.UnauthorizedException;
import com.see.realview._core.utils.ExceptionResponseWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ExceptionResponseWriter responseWriter;


    public JwtExceptionFilter(@Autowired ExceptionResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch (UnauthorizedException exception) {
            responseWriter.write(response, exception);
        }
        catch (JWTCreationException | JWTVerificationException exception) {
            responseWriter.write(response, new UnauthorizedException(ExceptionStatus.TOKEN_PARSING_ERROR));
        }
    }
}
