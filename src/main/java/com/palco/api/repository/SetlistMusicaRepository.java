package com.palco.api.repository;

import com.palco.api.model.SetlistMusica;
import com.palco.api.model.SetlistMusicaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetlistMusicaRepository extends JpaRepository<SetlistMusica, SetlistMusicaId> {
}
