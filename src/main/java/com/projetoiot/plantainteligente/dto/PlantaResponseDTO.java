package com.projetoiot.plantainteligente.dto;

import lombok.Data;

@Data
public class PlantaResponseDTO {
    private Long id;
    private Long usuarioId;
    private String nomePlanta;
    private String icone;
    private String umidadePlanta;
    private String tempPlanta;
    private String solPlanta;
}
