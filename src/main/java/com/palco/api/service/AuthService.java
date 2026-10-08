package com.palco.api.service;

import com.palco.api.dto.request.LoginRequest;
import com.palco.api.dto.request.RefreshTokenRequest;
import com.palco.api.dto.request.RegistroRequest;
import com.palco.api.dto.response.TokenResponse;
import com.palco.api.dto.response.UsuarioResponse;
import com.palco.api.exception.CredenciaisInvalidasException;
import com.palco.api.exception.EmailJaCadastradoException;
import com.palco.api.exception.TokenInvalidoException;
import com.palco.api.model.RefreshToken;
import com.palco.api.model.Usuario;
import com.palco.api.repository.RefreshTokenRepository;
import com.palco.api.repository.UsuarioRepository;
import com.palco.api.security.JwtService;
import com.palco.api.security.TokenHasher;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;

    private static final String MENSAGEM_TOKEN_INVALIDO =
            "Refresh token inválido, expirado, revogado ou já utilizado";

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
        String refreshToken = gerarESalvarRefreshToken(usuarioEncontrado);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setExpiraEm(jwtService.getExpiracaoAccessTokenEmSegundos());

        return tokenResponse;
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        boolean tipoValido;
        try {
            tipoValido = jwtService.isRefreshToken(token);
        } catch (JwtException e) {
            throw new TokenInvalidoException(MENSAGEM_TOKEN_INVALIDO);
        }

        if (!tipoValido) {
            throw new TokenInvalidoException(MENSAGEM_TOKEN_INVALIDO);
        }

        RefreshToken registro = refreshTokenRepository.findByTokenHash(tokenHasher.hash(token))
                .orElseThrow(() -> new TokenInvalidoException(MENSAGEM_TOKEN_INVALIDO));

        Usuario usuario = registro.getUsuario();
        refreshTokenRepository.delete(registro);

        String accessToken = jwtService.gerarAccessToken(usuario.getEmail());
        String novoRefreshToken = gerarESalvarRefreshToken(usuario);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(novoRefreshToken);
        tokenResponse.setExpiraEm(jwtService.getExpiracaoAccessTokenEmSegundos());

        return tokenResponse;
    }

    private String gerarESalvarRefreshToken(Usuario usuario) {
        String token = jwtService.gerarRefreshToken(usuario.getEmail());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setTokenHash(tokenHasher.hash(token));
        refreshToken.setExpiraEm(jwtService.extrairExpiracao(token));
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public void logout(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        refreshTokenRepository.findByTokenHash(tokenHasher.hash(token))
                .ifPresent(refreshTokenRepository::delete);
    }
}
