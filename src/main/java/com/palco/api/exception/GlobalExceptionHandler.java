package com.palco.api.exception;

import com.palco.api.dto.response.ErroResposta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String EMAIL_JA_CADASTRADO = "EMAIL_JA_CADASTRADO";
    private static final String CREDENCIAIS_INVALIDAS = "CREDENCIAIS_INVALIDAS";
    private static final String TOKEN_INVALIDO = "TOKEN_INVALIDO";
    private static final String DADOS_INVALIDOS = "DADOS_INVALIDOS";

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResposta> tratarEmailJaCadastrado(EmailJaCadastradoException e) {
        return montarResposta(HttpStatus.CONFLICT, EMAIL_JA_CADASTRADO, e.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResposta> tratarCredenciaisInvalidas(CredenciaisInvalidasException e) {
        return montarResposta(HttpStatus.UNAUTHORIZED, CREDENCIAIS_INVALIDAS, e.getMessage());
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErroResposta> tratarTokenInvalido(TokenInvalidoException e) {
        return montarResposta(HttpStatus.UNAUTHORIZED, TOKEN_INVALIDO, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return montarResposta(HttpStatus.BAD_REQUEST, DADOS_INVALIDOS, "Dados de entrada inválidos");
    }


    private ResponseEntity<ErroResposta> montarResposta(HttpStatus httpStatus, String codigo, String mensagem) {
        ErroResposta erroResposta = new ErroResposta();
        erroResposta.setMensagem(mensagem);
        erroResposta.setCodigo(codigo);
        erroResposta.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(httpStatus).body(erroResposta);
    }
}
