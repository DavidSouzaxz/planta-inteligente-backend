package com.projetoiot.plantainteligente.repository;

import com.projetoiot.plantainteligente.entity.HistoricoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoricoEventoRepository extends JpaRepository<HistoricoEvento, Long> {
    

    List<HistoricoEvento> findTop50ByOrderByIdDesc();
}