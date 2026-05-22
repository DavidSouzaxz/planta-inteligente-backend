package com.projetoiot.plantainteligente.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "planta_id", referencedColumnName = "id")
    private Planta planta;
}