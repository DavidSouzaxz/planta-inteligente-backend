package com.projetoiot.plantainteligente.controller;

import com.projetoiot.plantainteligente.entity.AlbumRegistro;
import com.projetoiot.plantainteligente.entity.LeituraSensor;
import com.projetoiot.plantainteligente.repository.AlbumRegistroRepository;
import com.projetoiot.plantainteligente.repository.LeituraSensorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/album")
@CrossOrigin(origins = "*")
@Tag(name = "Álbum e Diário Visual", description = "Endpoints para o diário de fotos e diagnósticos")
public class AlbumController {

    @Autowired
    private AlbumRegistroRepository albumRepository;

    @Autowired
    private LeituraSensorRepository leituraRepository;

    @PostMapping("/registrar")
    @Operation(summary = "Adiciona uma nova foto/relato ao álbum e gera uma sugestão baseada nos sensores")
    public ResponseEntity<AlbumRegistro> registrarDiario(@RequestBody AlbumRegistro registro) {
        

        LeituraSensor ultimaLeitura = leituraRepository.findFirstByOrderByIdDesc().orElse(null);
        

        String sugestao = gerarSugestaoDiagnostico(registro.getEstadoVisual(), ultimaLeitura);
        registro.setSugestaoSistema(sugestao);

        AlbumRegistro salvo = albumRepository.save(registro);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/todos")
    @Operation(summary = "Retorna todas as fotos e relatos cadastrados no álbum")
    public ResponseEntity<List<AlbumRegistro>> obterTodos() {
        return ResponseEntity.ok(albumRepository.findAllByOrderByIdDesc());
    }

    private String gerarSugestaoDiagnostico(String estadoVisual, LeituraSensor sensores) {
        if (sensores == null) {
            return "Registro salvo! Não consegui ler os sensores no momento para gerar um diagnóstico preciso.";
        }


        if ("MURCHANDO".equalsIgnoreCase(estadoVisual)) {
            if (sensores.getUmidadeSolo() < 25) {
                return "Diagnóstico: Confirmado! Suas folhas estão murchando porque a umidade do solo está muito baixa (" + sensores.getUmidadeSolo() + "%). Regue imediatamente.";
            } else if (sensores.getUmidadeSolo() > 80) {
                return "Diagnóstico Alerta: Suas folhas estão murchando, mas o solo está muito encharcado (" + sensores.getUmidadeSolo() + "%). Isso indica excesso de água, o que pode estar apodrecendo as raízes. Pare de regar!";
            }
        }

        if ("AMARELADA".equalsIgnoreCase(estadoVisual)) {
            if (sensores.getLuminosidade() > 2000) {
                return "Diagnóstico: Folhas amareladas combinadas com alta luminosidade (" + sensores.getLuminosidade() + " Lux) sugerem queimadura de sol. Mude a planta para um local com luz indireta.";
            } else {
                return "Diagnóstico: Folhas amareladas podem indicar falta de nutrientes na terra ou excesso de umidade acumulada. Monitore as regas.";
            }
        }

        return "Sua planta parece ótima visualmente e os sensores indicam parâmetros estáveis. Continue assim!";
    }
}