package com.projetoiot.plantainteligente.service;

import com.projetoiot.plantainteligente.repository.MonitoramentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;


@Service
public class LimpezaDadosService {

    @Autowired
    private MonitoramentoRepository repository;

    // Roda todos os dias exatamente às 03:00:00 da manhã
    @Scheduled(cron = "0 0 3 * * ?")
    public void limparDadosAntigos() {
        // Define o limite como o início do dia atual (00:00:00 de hoje)
        // Tudo o que tiver timestamp menor que isso pertence aos dias anteriores e será apagado
        LocalDateTime limite = LocalDate.now().atStartOfDay();

        repository.deleteByDataHoraBefore(limite);

        System.out.println("--- [IoT] Rotina das 03:00 executada: Dados dos dias anteriores removidos do Neon. ---");
    }
}