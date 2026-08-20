package com.palco.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@IdClass(SetlistMusicaId.class)
@Entity
@Table(name = "setlist_musica")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetlistMusica {
    @Id
    @ManyToOne
    @JoinColumn(name = "setlist_id", nullable = false)
    private Setlist setlist;

    @Id
    @ManyToOne
    @JoinColumn(name = "musica_id", nullable = false)
    private Musica musica;

    @Column(nullable = true)
    private Integer ordem;

    @Column(nullable = false)
    private Integer fonte;

    @Column(nullable = false)
    private String tom;

    @Column(nullable = false)
    private Integer velocidade;
}
