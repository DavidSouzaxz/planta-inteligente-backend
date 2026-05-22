package com.projetoiot.plantainteligente.repository;

import com.projetoiot.plantainteligente.entity.LeituraSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeituraSensorRepository extends JpaRepository<LeituraSensor, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM LeituraSensor l WHERE l.dataHora < :limite")
    void apagarLeiturasAntigas(LocalDateTime limite);

    Optional<LeituraSensor> findFirstByOrderByIdDesc();


    List<LeituraSensor> findTop30ByOrderByIdDesc();
}