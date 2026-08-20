package com.palco.api.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErroResposta {
    private String codigo;
    private String mensagem;
    private LocalDateTime timestamp;
}
