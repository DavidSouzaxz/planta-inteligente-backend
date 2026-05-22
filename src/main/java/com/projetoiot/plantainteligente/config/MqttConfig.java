package com.projetoiot.plantainteligente.config;
import com.fasterxml.jackson.databind.ObjectMapper;
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

                    LeituraSensor leitura = objectMapper.readValue(payload, LeituraSensor.class);
                    leituraRepository.save(leitura);


                    Optional<Planta> plantaOpt = plantaRepository.findById(1L);
                    if (plantaOpt.isPresent()) {
                        analisarEGerarEvento(leitura, plantaOpt.get());
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


        if (leitura.getUmidadeSolo() < 20) {
            estadoAtual = "SEDE";
            descricao = "Fiquei com muita sede! Preciso de água.";
        } else if (leitura.getUmidadeSolo() > 85) {
            estadoAtual = "MUITO_MOLHADA";
            descricao = "Minha terra ficou encharcada.";
        } else if ("SOMBRA".equalsIgnoreCase(planta.getTipoAmbiente()) && leitura.getLuminosidade() > 1500) {
            estadoAtual = "MUITO_SOL";
            descricao = "Levei muito sol direto nas minhas folhas!";
        } else if ("SOL".equalsIgnoreCase(planta.getTipoAmbiente()) && leitura.getLuminosidade() < 200) {
            estadoAtual = "MUITO_ESCURO";
            descricao = "Fiquei muito tempo no escuro absoluto.";
        }


        if (!estadoAtual.equals(ultimoEstadoConhecido)) {
            HistoricoEvento novoEvento = new HistoricoEvento();
            novoEvento.setTipoEvento(estadoAtual);
            novoEvento.setDescricao(descricao);

            eventoRepository.save(novoEvento);
            System.out.println("📌 [Novo Evento Registrado]: " + descricao);


            ultimoEstadoConhecido = estadoAtual;
        }
    }

}
