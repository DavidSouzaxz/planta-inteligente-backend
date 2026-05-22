package com.projetoiot.plantainteligente.repository;

import com.projetoiot.plantainteligente.entity.AlbumRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlbumRegistroRepository extends JpaRepository<AlbumRegistro, Long> {
    

    List<AlbumRegistro> findAllByOrderByIdDesc();
}