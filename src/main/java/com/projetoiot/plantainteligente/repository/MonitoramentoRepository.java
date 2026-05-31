package com.projetoiot.plantainteligente.repository;

import com.projetoiot.plantainteligente.entity.HistoricoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Repository
public interface MonitoramentoRepository extends JpaRepository<HistoricoEvento, Long> {
    
    // Este método diz ao JPA para criar a query: "DELETE FROM tb_monitoramento WHERE data_hora < ?"
    @Transactional
    void deleteByDataHoraBefore(LocalDateTime limite);
}