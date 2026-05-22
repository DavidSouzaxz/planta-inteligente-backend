package com.projetoiot.plantainteligente.controller;

import com.projetoiot.plantainteligente.entity.Planta;
import com.projetoiot.plantainteligente.repository.PlantaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/planta")
@CrossOrigin(origins = "*")
@Tag(name = "Configuração da Planta", description = "Endpoints para o Quiz e perfil da planta")
public class PlantaController {

    @Autowired
    private PlantaRepository repository;

    @PostMapping("/configurar")
    @Operation(summary = "Salva as respostas do Quiz inicial e cria o perfil da planta")
    public ResponseEntity<Planta> configurarPlanta(@RequestBody Planta planta) {
        Planta novaPlanta = repository.save(planta);
        return ResponseEntity.ok(novaPlanta);
    }
}