package com.palco.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiracao-access-token}")
    private Long jwtExpiracaoAccessToken;

    @Value("${jwt.expiracao-refresh-token}")
    private Long jwtExpiracaoRefreshToken;

    private static final String TIPO_ACCESS = "access";
    private static final String TIPO_REFRESH = "refresh";
    private static final String CLAIM_TIPO = "tipo";

    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    private String gerarToken(String email, Long expiracaoEmMs, String tipo) {
        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claim(CLAIM_TIPO, tipo)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracaoEmMs))
                .signWith(getKey())
                .compact();
        return token;
    }

    public String gerarAccessToken(String email) {
        return gerarToken(email, jwtExpiracaoAccessToken, TIPO_ACCESS);
    }

    public String gerarRefreshToken(String email) {
        return gerarToken(email, jwtExpiracaoRefreshToken, TIPO_REFRESH);
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    private Claims extrairClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims;
    }

    public String extrairTipo(String token) {
        return extrairClaims(token)
                .get(CLAIM_TIPO, String.class);
    }

    public Long getExpiracaoAccessTokenEmSegundos() {
        return jwtExpiracaoAccessToken/1000;
    }

    public boolean isRefreshToken(String token) {
        return TIPO_REFRESH.equals(extrairTipo(token));
    }

    public boolean isAccessToken(String token) {
        return TIPO_ACCESS.equals(extrairTipo(token));
    }

    public LocalDateTime extrairExpiracao(String token) {
        Date expiracao = extrairClaims(token).getExpiration();
        Instant instante = expiracao.toInstant();
        return LocalDateTime.ofInstant(instante, ZoneId.systemDefault());
    }
}
