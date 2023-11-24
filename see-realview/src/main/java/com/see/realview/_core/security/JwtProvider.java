package com.see.realview._core.security;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview._core.exception.client.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    public final static String AUTHORIZATION_HEADER = "Authorization";

    public final static String REFRESH_HEADER = "Refresh";

    public final static String TOKEN_PREFIX = "Bearer ";

    private final static Long ACCESS_EXP = 1000L * 60 * 30;

    private final static Long REFRESH_EXP = 1000L * 60 * 60 * 24 * 14;

    @Value("${security.jwt.secret.access}")
    private String ACCESS_SECRET;

    @Value("${security.jwt.secret.refresh}")
    private String REFRESH_SECRET;

    public String createAccessToken(Long userId) {
        return create(userId, ACCESS_EXP, ACCESS_SECRET);
    }

    public String createRefreshToken(Long userId) {
        return create(userId, REFRESH_EXP, REFRESH_SECRET);
    }

    public Long verifyAccessToken(String token) {
        return verify(token, ACCESS_SECRET);
    }

    public Long verifyRefreshToken(String token) {
        return verify(token, REFRESH_SECRET);
    }

    private Long verify(String token, String secret) {
        token = token.replace(TOKEN_PREFIX, "");
        Key key = getKey(secret);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJwt(token)
                    .getBody();

            return (Long) claims.get("id");
        }
        catch (ExpiredJwtException exception) {
            throw new UnauthorizedException(ExceptionStatus.ACCESS_TOKEN_EXPIRED);
        }
        catch (SignatureException exception) {
            throw new UnauthorizedException(ExceptionStatus.SIGNATURE_ERROR);
        }
        catch (JwtException exception) {
            throw new ServerException(ExceptionStatus.TOKEN_PARSING_ERROR);
        }
    }

    private static Key getKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String create(Long userId, Long EXP, String secret) {
        Map<String, Object> headers = createHeader();
        Map<String, Object> claims = createClaims(userId);

        Date expiredAt = new Date();
        expiredAt.setTime(expiredAt.getTime() + EXP);

        Key key = getKey(secret);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setExpiration(expiredAt)
                .signWith(key, signatureAlgorithm);

        return jwtBuilder.compact();
    }

    private static Map<String, Object> createHeader() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "jwt");
        headers.put("alg", "HS512");
        return headers;
    }

    private static Map<String, Object> createClaims(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        return claims;
    }
}
