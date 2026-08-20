package com.palco.api.repository;

import com.palco.api.model.Setlist;
import com.palco.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetlistRepository extends JpaRepository<Setlist, Long> {
    List<Setlist> findByUsuario(Usuario usuario);
}
