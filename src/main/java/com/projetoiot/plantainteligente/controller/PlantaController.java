package com.projetoiot.plantainteligente.controller;

import com.projetoiot.plantainteligente.entity.Planta;
import com.projetoiot.plantainteligente.repository.PlantaRepository;
import com.projetoiot.plantainteligente.service.PlantaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/planta")
@CrossOrigin(origins = "*")
@Tag(name = "Configuração da Planta", description = "Endpoints para o Quiz e perfil da planta")
public class PlantaController {

    @Autowired
    private PlantaRepository repository;

    @Autowired
    private PlantaService service;

    @GetMapping
    @Operation(summary = "Lista todas as plantas")
    public ResponseEntity<List<Planta>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "lista plantas pelo id")
    public ResponseEntity<Optional<Planta>> getOne(@RequestParam Long idPlanta) {
        return ResponseEntity.ok(repository.findById(idPlanta));
    }

    @PostMapping("/configurar")
    @Operation(summary = "Salva as respostas do Quiz inicial e cria o perfil da planta")
    public ResponseEntity<Planta> configurarPlanta(@RequestBody Planta planta) {
        Planta novaPlanta = repository.save(planta);
        return ResponseEntity.ok(novaPlanta);
    }

    @PatchMapping("/{id}/configurar")
    @Operation(summary = "Salva as respostas do Quiz inicial e edita o perfil da planta")
    public ResponseEntity<Planta> editarPlanta(@RequestBody Planta planta, @PathVariable Long id) {
        Planta novaPlanta = service.salvarOuAtualizarPlanta(id, planta);
        return ResponseEntity.ok(novaPlanta);
    }
}