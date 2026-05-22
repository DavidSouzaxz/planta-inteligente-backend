package com.projetoiot.plantainteligente.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_album_registros")
@Data
public class AlbumRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String fotoBase64;

    private String estadoVisual;
    
    @Column(columnDefinition = "TEXT")
    private String relatoUsuario;

    @Column(columnDefinition = "TEXT")
    private String sugestaoSistema;

    private LocalDateTime dataRegistro = LocalDateTime.now();
}