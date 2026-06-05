package com.projetoiot.plantainteligente.controller;

import com.projetoiot.plantainteligente.dto.PlantaRequestDTO;
import com.projetoiot.plantainteligente.dto.PlantaResponseDTO;
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
    public ResponseEntity<List<PlantaResponseDTO>> getAll() {
        return ResponseEntity.ok(service.listarPlantas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "lista plantas pelo id")
    public ResponseEntity<List<PlantaResponseDTO>> getOne(@RequestParam Long idPlanta) {
        return ResponseEntity.ok(service.listarPlantasPorUsuario(idPlanta));
    }

    @GetMapping("/usuario/{id}")
    @Operation(summary = "lista plantas pelo id do usuario")
    public ResponseEntity<List<PlantaResponseDTO>> getAllByUser(@RequestParam Long idUser) {
        return ResponseEntity.ok(service.listarPlantasPorUsuario(idUser));
    }
    @PostMapping("/configurar")
    @Operation(summary = "Salva as respostas do Quiz inicial e cria o perfil da planta")
    public ResponseEntity<PlantaResponseDTO> configurarPlanta(@RequestBody PlantaRequestDTO planta) {
        PlantaResponseDTO novaPlanta = service.salvar(planta);
        return ResponseEntity.ok(novaPlanta);
    }

    @PatchMapping("/{id}/configurar")
    @Operation(summary = "Salva as respostas do Quiz inicial e edita o perfil da planta")
    public ResponseEntity<PlantaResponseDTO> editarPlanta(@RequestBody PlantaRequestDTO planta, @PathVariable Long id) {
        PlantaResponseDTO novaPlanta = service.salvarOuAtualizarPlanta(id, planta);
        return ResponseEntity.ok(novaPlanta);
    }
}