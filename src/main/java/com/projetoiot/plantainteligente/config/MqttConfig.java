package com.projetoiot.plantainteligente.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetoiot.plantainteligente.dto.PlantaRequestDTO;
import com.projetoiot.plantainteligente.dto.PlantaResponseDTO;
import com.projetoiot.plantainteligente.entity.HistoricoEvento;
import com.projetoiot.plantainteligente.entity.LeituraSensor;
import com.projetoiot.plantainteligente.entity.Planta;
import com.projetoiot.plantainteligente.repository.HistoricoEventoRepository;
import com.projetoiot.plantainteligente.repository.LeituraSensorRepository;
import com.projetoiot.plantainteligente.repository.PlantaRepository;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MqttConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;

    @Autowired
    private LeituraSensorRepository leituraRepository;

    @Autowired
    private PlantaRepository plantaRepository;

    @Autowired
    private HistoricoEventoRepository eventoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String ultimoEstadoConhecido = "DESCONHECIDO";

    @EventListener(ApplicationReadyEvent.class)
    public void iniciarConexaoMqtt() {
        try {
            MqttClient client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);

            System.out.println("Tentando conectar nativamente ao HiveMQ Cloud...");
            client.connect(options);
            System.out.println("✅ CONECTADO AO HIVEMQ CLOUD COM SUCESSO!");

            client.subscribe(topic, (tp, msg) -> {
                String payload = new String(msg.getPayload());

                try {
                    // Garante o salvamento bruto independente de qualquer regra do quiz
                    LeituraSensor leitura = objectMapper.readValue(payload, LeituraSensor.class);
                    leituraRepository.save(leitura);

                    // AJUSTE: Busca a planta mais recente cadastrada no sistema para avaliar
                    // (evita travar fixo no ID 1L caso ele tenha sido deletado)
                    List<Planta> plantas = plantaRepository.findAll();
                    if (!plantas.isEmpty()) {
                        // Avalia com base na última planta ativa do banco
                        Planta plantaAtiva = plantas.get(plantas.size() - 1);
                        analisarEGerarEvento(leitura, plantaAtiva);
                    }

                } catch (Exception e) {
                    System.err.println("❌ Erro ao processar dados MQTT: " + e.getMessage());
                }
            });

        } catch (MqttException e) {
            System.err.println("❌ Erro ao conectar ao HiveMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void analisarEGerarEvento(LeituraSensor leitura, Planta planta) {
        String estadoAtual = "OK";
        String descricao = "Estou me sentindo muito bem!";

        if ("Raramente".equalsIgnoreCase(planta.getUmidadePlanta()) && leitura.getUmidadeSolo() > 45) {
            estadoAtual = "MUITO_MOLHADA";
            descricao = "Eu prefiro solo mais seco (Raramente), mas minha terra está muito úmida!";
        } else if ("Frequentemente".equalsIgnoreCase(planta.getUmidadePlanta()) && leitura.getUmidadeSolo() < 40) {
            estadoAtual = "SEDE";
            descricao = "Eu gosto de regas frequentes. Minha terra está ficando seca!";
        }
        else if (leitura.getUmidadeSolo() < 20) {
            estadoAtual = "SEDE";
            descricao = "Alerta crítico: Minha terra secou completamente!";
        } else if (leitura.getUmidadeSolo() > 85) {
            estadoAtual = "MUITO_MOLHADA";
            descricao = "Alerta crítico: Minhas raízes estão afogadas na água.";
        }
        else if ("Pouco Sol".equalsIgnoreCase(planta.getSolPlanta()) && leitura.getLuminosidade() > 800) {
            estadoAtual = "MUITO_SOL";
            descricao = "Fui configurada para Pouco Sol, mas a claridade aqui está excessiva!";
        } else if ("Muito Sol".equalsIgnoreCase(planta.getSolPlanta()) && leitura.getLuminosidade() < 400) {
            estadoAtual = "MUITO_ESCURO";
            descricao = "Eu amo Muito Sol, mas este ambiente está escuro demais para mim.";
        }
        else if (planta.getTempPlanta() != null) {
            try {
                String tempNumerica = planta.getTempPlanta().replaceAll("[^0-9]", "");
                double temperaturaIdeal = Double.parseDouble(tempNumerica);

                if (leitura.getTemperatura() > (temperaturaIdeal + 5.0)) {
                    estadoAtual = "MUITO_QUENTE";
                    descricao = "Está muito quente! Passou do meu limite ideal de " + planta.getTempPlanta();
                } else if (leitura.getTemperatura() < (temperaturaIdeal - 5.0)) {
                    estadoAtual = "MUITO_FRIO";
                    descricao = "Estou sentindo frio! A temperatura caiu muito abaixo de " + planta.getTempPlanta();
                }
            } catch (Exception e) {
                System.err.println("⚠️ Falha ao fazer o parse da temperatura do quiz: " + planta.getTempPlanta());
            }
        }

        if (!estadoAtual.equals(ultimoEstadoConhecido)) {
            HistoricoEvento novoEvento = new HistoricoEvento();
            novoEvento.setTipoEvento(estadoAtual);
            novoEvento.setDescricao(descricao);

            eventoRepository.save(novoEvento);
            System.out.println("📌 [Novo Evento Registrado com base no Quiz]: " + descricao);

            ultimoEstadoConhecido = estadoAtual;
        }
    }
}
