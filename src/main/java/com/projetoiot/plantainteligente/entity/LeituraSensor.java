package com.projetoiot.plantainteligente.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_leituras_sensores")
@Data
public class LeituraSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Double temperatura;
    private Double umidadeAr;
    private Double luminosidade;
    private Integer umidadeSolo;


    private LocalDateTime dataHora = LocalDateTime.now();
}
