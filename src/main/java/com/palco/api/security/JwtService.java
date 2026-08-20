package com.palco.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiracao-access-token}")
    private Long jwtExpiracaoAccessToken;

    @Value("${jwt.expiracao-refresh-token}")
    private Long jwtExpiracaoRefreshToken;

    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    private String gerarToken(String email, Long expiracaoEmMs) {
        String token = Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracaoEmMs))
                .signWith(getKey())
                .compact();
        return token;
    }

    public String gerarAccessToken(String email) {
        return gerarToken(email, jwtExpiracaoAccessToken);
    }

    public String gerarRefreshToken(String email) {
        return gerarToken(email, jwtExpiracaoRefreshToken);
    }

    public String extrairEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}
