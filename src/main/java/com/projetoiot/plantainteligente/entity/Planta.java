package com.projetoiot.plantainteligente.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_plantas")
@Data
public class Planta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    private String nomePlanta;
    private String icone;
    private String umidadePlanta;
    private String tempPlanta;
    private String solPlanta;

}