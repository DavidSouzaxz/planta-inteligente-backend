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

    private String nomeUsuario;
    private String nomePlanta;
    private String icone;
    
    private String tipoAmbiente;
}