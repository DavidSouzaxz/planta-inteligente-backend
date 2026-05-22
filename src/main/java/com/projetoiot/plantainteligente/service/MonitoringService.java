package com.projetoiot.plantainteligente.service;

import com.projetoiot.plantainteligente.dto.HomeResponseDTO;
import com.projetoiot.plantainteligente.entity.LeituraSensor;
import com.projetoiot.plantainteligente.entity.Planta;
import com.projetoiot.plantainteligente.repository.LeituraSensorRepository;
import com.projetoiot.plantainteligente.repository.PlantaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
    public HomeResponseDTO obterDadosHome() {

        Planta planta = plantaRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Por favor, configure o Quiz primeiro!"));


        LeituraSensor ultimaLeitura = leituraRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("Nenhum dado recebido do ESP32 ainda."));


        HomeResponseDTO home = new HomeResponseDTO();
        home.setNomeUsuario(planta.getNomeUsuario());
        home.setNomePlanta(planta.getNomePlanta());
        home.setIcone(planta.getIcone());
        home.setTemperatura(ultimaLeitura.getTemperatura());
        home.setUmidadeAr(ultimaLeitura.getUmidadeAr());
        home.setLuminosidade(ultimaLeitura.getLuminosidade());
        home.setUmidadeSolo(ultimaLeitura.getUmidadeSolo());


        definirHumorEAlerta(home, planta.getTipoAmbiente());

        return home;
    }

    private void definirHumorEAlerta(HomeResponseDTO home, String tipoAmbiente) {

        if (home.getUmidadeSolo() < 20) {
            home.setHumor("SEDE");
            home.setAlerta("Estou com muita sede! Coloque um pouco de água na minha terra, por favor.");
            return;
        }


        if (home.getUmidadeSolo() > 85) {
            home.setHumor("MUITO_MOLHADA");
            home.setAlerta("Minha terra está encharcada. Cuidado para não apodrecer minhas raízes!");
            return;
        }


        if ("SOMBRA".equalsIgnoreCase(tipoAmbiente) && home.getLuminosidade() > 1500) {
            home.setHumor("MUITO_SOL");
            home.setAlerta("Está muito quente aqui! Minhas folhas podem queimar, me mude para a sombra.");
            return;
        }


        if ("SOL".equalsIgnoreCase(tipoAmbiente) && home.getLuminosidade() < 200) {
            home.setHumor("MUITO_ESCURO");
            home.setAlerta("Está muito escuro... Preciso de um pouco de luz solar para fazer fotossíntese.");
            return;
        }


        home.setHumor("FELIZ");
        home.setAlerta("Estou ótima! Obrigado por cuidar tão bem de mim.");
    }
}
