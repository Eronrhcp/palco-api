package com.palco.api.exception;

import com.palco.api.dto.request.RefreshTokenRequest;
import com.palco.api.dto.response.TokenResponse;
import com.palco.api.security.JwtService;
import io.jsonwebtoken.JwtException;

public class TokenInvalidoException extends RuntimeException {
    public TokenInvalidoException(String mensagem) {
        super(mensagem);
    }
}
