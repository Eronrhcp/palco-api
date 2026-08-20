package com.palco.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cifra")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cifra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "musica_id", nullable = false, updatable = false)
    private Musica musica;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String corpoCifra;

    @Column(updatable = false)
    private String linkOrigem;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private OrigemCifra origem;
}
