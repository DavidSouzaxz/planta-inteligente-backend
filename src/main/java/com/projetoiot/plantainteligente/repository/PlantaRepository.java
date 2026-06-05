package com.projetoiot.plantainteligente.repository;

import com.projetoiot.plantainteligente.dto.PlantaRequestDTO;
import com.projetoiot.plantainteligente.dto.PlantaResponseDTO;
import com.projetoiot.plantainteligente.entity.Planta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantaRepository extends JpaRepository<Planta, Long> {
    Optional<Planta> findByUsuarioId(Long id);
}