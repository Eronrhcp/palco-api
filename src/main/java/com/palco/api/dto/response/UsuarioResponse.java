package com.palco.api.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UsuarioResponse {
    private Long id;
    private String email;
    private String imagem;
    private LocalDateTime criadoEm;
}
