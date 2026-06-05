package com.projetoiot.plantainteligente.dto;

import lombok.Data;

@Data
public class HomeResponseDTO {
    private Long usuarioId;
    private String nomePlanta;
    private String icone;


    private Double temperatura;
    private Double umidadeAr;
    private Double luminosidade;
    private Integer umidadeSolo;


    private String humor;
    private String alerta;
}