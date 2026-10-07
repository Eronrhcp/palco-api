package com.palco.api.service;

import com.palco.api.dto.request.LoginRequest;
import com.palco.api.dto.request.RefreshTokenRequest;
import com.palco.api.dto.request.RegistroRequest;
import com.palco.api.dto.response.TokenResponse;
import com.palco.api.dto.response.UsuarioResponse;
import com.palco.api.exception.CredenciaisInvalidasException;
import com.palco.api.exception.EmailJaCadastradoException;
import com.palco.api.exception.TokenInvalidoException;
import com.palco.api.model.Usuario;
import com.palco.api.repository.UsuarioRepository;
import com.palco.api.security.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioResponse registrar(RegistroRequest request) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioExistente.isPresent()) {
            throw new EmailJaCadastradoException("Já existe um usuário cadastrado com esse e-mail.");
        }
        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(request.getEmail());
        novoUsuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuarioSalvo.getId());
        response.setEmail(usuarioSalvo.getEmail());
        response.setImagem(usuarioSalvo.getImagem());
        response.setCriadoEm(usuarioSalvo.getCriadoEm());

        return response;
    }

    public TokenResponse login(LoginRequest request) {
        Usuario usuarioEncontrado = usuarioRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new CredenciaisInvalidasException("Credenciais inválidas"));

        boolean senhaCorreta = passwordEncoder.matches(request.getSenha(), usuarioEncontrado.getSenhaHash());

        if (!senhaCorreta) {
            throw new CredenciaisInvalidasException("Credenciais inválidas");
        }

        String accessToken = jwtService.gerarAccessToken(usuarioEncontrado.getEmail());
        String refreshToken = jwtService.gerarRefreshToken(usuarioEncontrado.getEmail());

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setExpiraEm(jwtService.getExpiracaoAccessTokenEmSegundos());

        return tokenResponse;
    }

    public TokenResponse refresh(RefreshTokenRequest request) {
        String email;
        try {
            email = jwtService.extrairEmail(request.getRefreshToken());

        } catch (JwtException e) {
            throw new TokenInvalidoException("Refresh token inválido ou expirado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new TokenInvalidoException("Refresh token inválido ou expirado"));

        String accessToken = jwtService.gerarAccessToken(usuario.getEmail());

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(request.getRefreshToken());
        tokenResponse.setExpiraEm(jwtService.getExpiracaoAccessTokenEmSegundos());

        return tokenResponse;
    }
}
