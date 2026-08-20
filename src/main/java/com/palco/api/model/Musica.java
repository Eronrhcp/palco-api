package com.palco.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "musica")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Musica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String artista;

    @Column(nullable = false)
    private String tomOriginal;

    @Column(nullable = true)
    private Integer ultimaFonteUsada;

    @Column(nullable = true)
    private String ultimoTomUsado;

    @Column(nullable = true)
    private Integer ultimaVelocidadeUsada;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @OneToOne(mappedBy = "musica")
    private Cifra cifra;
}
