package com.projetoiot.plantainteligente.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_eventos")
@Data
public class HistoricoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoEvento;
    private String descricao;
    private LocalDateTime dataHora = LocalDateTime.now();
}