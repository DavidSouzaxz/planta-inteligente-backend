package com.projetoiot.plantainteligente.repository;

import com.projetoiot.plantainteligente.entity.Planta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantaRepository extends JpaRepository<Planta, Long> {
}