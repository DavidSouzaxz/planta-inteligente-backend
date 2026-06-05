package com.projetoiot.plantainteligente.service;

import com.projetoiot.plantainteligente.dto.HomeResponseDTO;
import com.projetoiot.plantainteligente.dto.PlantaResponseDTO;
import com.projetoiot.plantainteligente.entity.LeituraSensor;
import com.projetoiot.plantainteligente.entity.Planta;
import com.projetoiot.plantainteligente.repository.LeituraSensorRepository;
import com.projetoiot.plantainteligente.repository.PlantaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MonitoringService {
    @Autowired
    private LeituraSensorRepository leituraRepository;

    @Autowired
    private PlantaRepository plantaRepository;

    public LeituraSensor obterStatusAtual() {
        return leituraRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("Nenhuma leitura encontrada no banco de dados."));
    }

    public List<LeituraSensor> obterHistoricoGrafico() {
        List<LeituraSensor> historico = leituraRepository.findTop30ByOrderByIdDesc();
        Collections.reverse(historico);
        return historico;
    }

    public HomeResponseDTO obterDadosHome(Long plantaId) {
        // 1. Busca a planta solicitada pelo ID informado
        Planta planta = plantaRepository.findById(plantaId)
                .orElseThrow(() -> new RuntimeException("Planta não encontrada ou Quiz não configurado!"));

        HomeResponseDTO home = new HomeResponseDTO();

        // Mapeia os dados fixos que já existem no banco (vindos do Quiz)
        if (planta.getUsuarioId() != null) {
            home.setUsuarioId(planta.getUsuarioId());
        }
        home.setNomePlanta(planta.getNomePlanta());
        home.setIcone(planta.getIcone());

        // 2. Busca a última leitura de forma segura usando Optional
        Optional<LeituraSensor> ultimaLeituraOpt = leituraRepository.findFirstByOrderByIdDesc();

        if (ultimaLeituraOpt.isEmpty()) {
            // Se o banco estiver vazio (ex: após a limpeza das 03:00), envia o status de VAZIO para o front
            home.setTemperatura(0.0);
            home.setUmidadeAr(0.0);
            home.setLuminosidade(0.0);
            home.setUmidadeSolo(0);

            home.setHumor("VAZIO"); // <--- Status tratado para o Front-end identificar
            home.setAlerta("Aguardando o primeiro envio de dados do ESP32 para este dispositivo...");

            return home;
        }

        // 3. Caso existam dados, extrai a leitura normalmente
        LeituraSensor ultimaLeitura = ultimaLeituraOpt.get();

        home.setTemperatura(ultimaLeitura.getTemperatura());
        home.setUmidadeAr(ultimaLeitura.getUmidadeAr());
        home.setLuminosidade(ultimaLeitura.getLuminosidade());
        home.setUmidadeSolo(ultimaLeitura.getUmidadeSolo());

        // Processa o humor cruzando os dados reais com o Quiz
        processarHumorEAlerta(home, planta);

        return home;
    }

    private void processarHumorEAlerta(HomeResponseDTO home, Planta planta) {

        // ==========================================
        // 1. UMIDADE DO SOLO (Mapeado com o Front)
        // ==========================================
        if ("Raramente".equalsIgnoreCase(planta.getUmidadePlanta()) && home.getUmidadeSolo() > 45) {
            home.setHumor("MUITO_MOLHADA");
            home.setAlerta("Eu prefiro solo mais seco (Raramente), mas minha terra está muito úmida!");
            return;
        } else if ("Frequentemente".equalsIgnoreCase(planta.getUmidadePlanta()) && home.getUmidadeSolo() < 40) {
            home.setHumor("SEDE");
            home.setAlerta("Eu gosto de regas frequentes. Minha terra está ficando seca!");
            return;
        }
        // Travas globais de segurança (Independentes do Quiz)
        else if (home.getUmidadeSolo() < 20) {
            home.setHumor("SEDE");
            home.setAlerta("Estou com muita sede! Coloque um pouco de água na minha terra, por favor.");
            return;
        } else if (home.getUmidadeSolo() > 85) {
            home.setHumor("MUITO_MOLHADA");
            home.setAlerta("Minha terra está encharcada. Cuidado para não apodrecer minhas raízes!");
            return;
        }

        // ==========================================
        // 2. LUMINOSIDADE (Mapeado com o Front)
        // ==========================================
        if ("Pouco Sol".equalsIgnoreCase(planta.getSolPlanta()) && home.getLuminosidade() > 800) {
            home.setHumor("MUITO_SOL");
            home.setAlerta("Fui configurada para Pouco Sol, mas a claridade aqui está excessiva! Me mude de lugar.");
            return;
        }
        if ("Muito Sol".equalsIgnoreCase(planta.getSolPlanta()) && home.getLuminosidade() < 400) {
            home.setHumor("MUITO_ESCURO");
            home.setAlerta("Eu amo Muito Sol, mas este ambiente está escuro demais para mim.");
            return;
        }

        // ==========================================
        // 3. TEMPERATURA DINÂMICA (Slider "XX°C")
        // ==========================================
        if (planta.getTempPlanta() != null) {
            try {
                // Isola os números da String (Ex: "25°C" vira "25")
                String tempNumerica = planta.getTempPlanta().replaceAll("[^0-9]", "");
                double temperaturaIdeal = Double.parseDouble(tempNumerica);

                // Aplica margem de segurança de 5°C tolerável
                if (home.getTemperatura() > (temperaturaIdeal + 5.0)) {
                    home.setHumor("MUITO_QUENTE");
                    home.setAlerta("Está calor demais! Passou do meu limite ideal configurado de " + planta.getTempPlanta());
                    return;
                } else if (home.getTemperatura() < (temperaturaIdeal - 5.0)) {
                    home.setHumor("MUITO_FRIO");
                    home.setAlerta("Estou sentindo muito frio! A temperatura caiu muito abaixo de " + planta.getTempPlanta());
                    return;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Falha ao ler a temperatura do quiz: " + planta.getTempPlanta());
            }
        }

        // Estado padrão caso tudo esteja perfeito
        home.setHumor("FELIZ");
        home.setAlerta("Estou ótima! Obrigado por cuidar tão bem de mim.");
    }
}
