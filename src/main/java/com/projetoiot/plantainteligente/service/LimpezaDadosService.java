package com.projetoiot.plantainteligente.service;

import com.projetoiot.plantainteligente.repository.LeituraSensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class LimpezaDadosService {

    @Autowired
    private LeituraSensorRepository repository;


    @Scheduled(fixedRate = 86400000)
    public void limparHistoricoAntigo() {

        LocalDateTime limite = LocalDateTime.now().minusDays(7);
        
        repository.apagarLeiturasAntigas(limite);
        
        System.out.println("🧹 [Limpeza] Dados com mais de 7 dias foram apagados para otimizar o banco!");
    }
}