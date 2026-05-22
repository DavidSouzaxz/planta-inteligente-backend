# 🌿 Planta Inteligente API (Backend)

O **Planta Inteligente API** é o núcleo de controle de um ecossistema IoT focado no monitoramento afetivo e gamificado de plantas. O sistema recebe dados brutos de sensores de hardware via protocolo MQTT (HiveMQ Cloud), processa as informações em tempo real e expõe uma API REST segura e tratada para consumo de uma aplicação Frontend (React / Mobile).

A grande inovação da arquitetura é o cruzamento de dados: o sistema define o "humor" da planta e gera diagnósticos dinâmicos para o diário de fotos baseando-se no perfil escolhido pelo usuário no Quiz inicial.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem Principal:** Java 17
- **Framework:** Spring Boot 3.x
  - *Spring Security* (Autenticação e controle de acessos)
  - *Spring Data JPA* (Persistência e comunicação com o banco)
- **Banco de Dados:** PostgreSQL (Pronto para produção / Render)
- **Segurança:** JWT (JSON Web Token) por meio da biblioteca `java-jwt` (Auth0)
- **Comunicação IoT:** Eclipse Paho MQTT Client (Integração com HiveMQ Cloud)
- **Documentação:** OpenAPI 3 / Swagger UI

---

## 📐 Arquitetura do Sistema e Fluxo de Dados

1. **Hardware (ESP32):** Coleta dados de Temperatura/Umidade (DHT22), Luminosidade (BH1750) e Umidade do Solo. Publica no broker HiveMQ via SSL de 5 em 5 segundos.
2. **Backend (Spring Boot):** Atua como um *subscriber* do tópico MQTT. Quando um dado chega, ele persiste a leitura bruta e analisa se houve mudança de estado crítico para gravar na linha do tempo de eventos.
3. **Segurança (JWT):** Protege as tabelas ocultando o banco de dados atrás de uma camada robusta de filtros. O usuário só interage com os endpoints se carregar um token válido gerado no Login.

---

## 🔐 Estrutura de Segurança (JWT)

Todas as rotas sob o prefixo `/api/monitoramento/**`, `/api/planta/**` e `/api/album/**` exigem autenticação. 
Para enviar requisições com sucesso, anexe o token recebido no Login ao cabeçalho HTTP de todas as chamadas:

- **Key:** `Authorization`
- **Value:** `Bearer SEU_TOKEN_JWT_AQUI`

---

## 🛣️ Principais Endpoints da API

Abaixo estão listadas as principais rotas disponíveis no sistema. Para detalhes de payloads e formatos de retorno, consulte a interface do Swagger.

### 👥 Autenticação (Rotas Públicas)
- `POST /api/auth/cadastro` - Registra um novo usuário com senha criptografada em BCrypt.
- `POST /api/auth/login` - Valida as credenciais e retorna o Token JWT de acesso.

### 📋 Quiz & Configuração Perfil (Protegido)
- `POST /api/planta/configurar` - Salva as respostas do quiz (Nome da planta, ambiente de Sol ou Sombra, e ícone escolhido).

### 🏠 Painel Home (Protegido)
- `GET /api/monitoramento/home` - Retorna o status atual dos sensores em tempo real já acompanhado do **Humor** calculado da planta (`FELIZ`, `SEDE`, `MUITO_SOL`, etc.) e uma mensagem de alerta amigável texturizada para o usuário.

### ⏳ Histórico e Diário Visual (Protegido)
- `GET /api/monitoramento/eventos` - Retorna uma linha do tempo enxuta contendo os eventos e as alterações críticas de ambiente sofridas pela planta.
- `POST /api/album/registrar` - Recebe o relato e foto em Base64 do usuário, cruza com o hardware na mesma estampa de tempo e grava uma sugestão de diagnóstico automático.
- `GET /api/album/todos` - Retorna o feed completo do álbum cronológico de fotos.

---

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
- Java 17 instalado.
- Maven 3.x instalado.
- Banco de Dados PostgreSQL configurado e rodando.

### 1. Clonar o repositório
```bash
git clone https://github.com/DavidSouzaxz/planta-inteligente-backend.git
cd planta-inteligente-backend
