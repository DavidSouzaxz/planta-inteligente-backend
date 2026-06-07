package com.projetoiot.plantainteligente.controller;

import com.projetoiot.plantainteligente.dto.HomeResponseDTO;
import com.projetoiot.plantainteligente.entity.HistoricoEvento;
import com.projetoiot.plantainteligente.entity.LeituraSensor;
import com.projetoiot.plantainteligente.repository.HistoricoEventoRepository;
import com.projetoiot.plantainteligente.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitoramento")
@CrossOrigin(origins = "*")
@Tag(name = "Monitoramento de Sensores", description = "Endpoints para obter dados do ESP32")
public class MonitoringController {

    @Autowired
    private MonitoringService service;

    @Autowired
    private HistoricoEventoRepository historicoEventoRepository;

    @GetMapping("/atual")
    @Operation(summary = "Busca o status atual em tempo real da planta")
    public ResponseEntity<LeituraSensor> obterStatusAtual() {
        return ResponseEntity.ok(service.obterStatusAtual());
    }

    @GetMapping("/historico")
    @Operation(summary = "Busca as últimas leituras para renderizar nos gráficos")
    public ResponseEntity<List<LeituraSensor>> obterHistorico() {
        return ResponseEntity.ok(service.obterHistoricoGrafico());
    }

    @GetMapping("/home/{id}")
    @Operation(summary = "Busca todos os dados tratados para a tela principal da Home (Humor, Alertas e Sensores)")
    public ResponseEntity<HomeResponseDTO> getHomeData(@PathVariable Long id) {
        HomeResponseDTO dadosHome = service.obterDadosHome(id);
        return ResponseEntity.ok(dadosHome);
    }

    @GetMapping("/eventos")
    @Operation(summary = "Retorna a linha do tempo com os eventos críticos que a planta passou")
    public ResponseEntity<List<HistoricoEvento>> obterLinhaDoTempo() {
        return ResponseEntity.ok(historicoEventoRepository.findTop50ByOrderByIdDesc());
    }
}