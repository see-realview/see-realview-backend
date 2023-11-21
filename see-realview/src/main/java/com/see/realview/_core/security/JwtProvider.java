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

    private final Key accessKey;

    private final Key refreshKey;

    public JwtProvider(@Value("${security.jwt.secret.access}") String ACCESS_SECRET,
                       @Value("${security.jwt.secret.refresh}") String REFRESH_SECRET) {
        this.accessKey = Keys.hmacShaKeyFor(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(REFRESH_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId) {
        return create(userId, ACCESS_EXP, accessKey);
    }

    public String createRefreshToken(Long userId) {
        return create(userId, REFRESH_EXP, refreshKey);
    }

    public Long verifyAccessToken(String token) {
        return verify(token, accessKey);
    }

    public Long verifyRefreshToken(String token) {
        return verify(token, refreshKey);
    }

    private Long verify(String token, Key key) {
        token = token.replace(TOKEN_PREFIX, "");

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

    private String create(Long userId, Long EXP, Key key) {
        Map<String, Object> headers = createHeader();
        Map<String, Object> claims = createClaims(userId);

        Date expiredAt = new Date();
        expiredAt.setTime(expiredAt.getTime() + EXP);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setExpiration(expiredAt)
                .signWith(key);

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
