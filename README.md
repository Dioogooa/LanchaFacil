
# 🍔 LanchaFacil

Sistema de gerenciamento de pedidos para lanchonetes, com backend em Spring Boot, mensageria via RabbitMQ, envio de e-mails e frontend em React.

Projeto desenvolvido como parte do programa **Let's Code**.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Principais Classes](#principais-classes)
- [Fluxo de Mensageria](#fluxo-de-mensageria)
- [Como Rodar](#como-rodar)
- [Configuração de E-mail](#configuração-de-e-mail)
- [Status do Projeto](#status-do-projeto)

---

## 📌 Sobre o Projeto

O **LanchaFacil** é uma aplicação fullstack para gerenciamento de uma lanchonete. Permite cadastrar clientes, registrar pedidos e acompanhar o status de preparo na cozinha em tempo real. A cada mudança de status de um pedido, uma notificação é publicada em uma fila do RabbitMQ e consumida por um serviço que envia e-mails automáticos para os clientes.

---

## 🛠️ Tecnologias Utilizadas

### Backend
| Tecnologia | Função |
|---|---|
| Java 17+ | Linguagem principal |
| Spring Boot | Framework base do backend |
| Spring Data JPA | Persistência e mapeamento ORM |
| Spring AMQP (RabbitMQ) | Mensageria assíncrona |
| Spring Mail | Envio de e-mails |
| PostgreSQL | Banco de dados relacional |
| Docker / Docker Compose | Orquestração de infraestrutura |

### Frontend
| Tecnologia | Função |
|---|---|
| React + Vite | Interface do usuário |
| Axios | Requisições HTTP para a API |
| JavaScript / CSS | Linguagem e estilização |

### Infraestrutura (Docker Compose)
- **PostgreSQL 16** — banco de dados rodando na porta `5433`
- **RabbitMQ 3** (com management) — broker de mensagens nas portas `5672` e `15672`

---

## 🏗️ Arquitetura

A aplicação segue uma arquitetura em camadas clássica do Spring Boot, separando responsabilidades de forma clara:

```
┌─────────────────────────────────────────┐
│              Frontend (React)            │
│   Clientes | Pedidos | Cozinha           │
└──────────────────┬──────────────────────┘
                   │ HTTP / REST (Axios)
┌──────────────────▼──────────────────────┐
│           Backend (Spring Boot)          │
│                                          │
│  Controllers REST                        │
│       ↓                                  │
│  Services (lógica de negócio)            │
│       ↓                                  │
│  Repositories (Spring Data JPA)          │
│       ↓                                  │
│  Entidades JPA ←→ PostgreSQL             │
│                                          │
│  Publishers → RabbitMQ → Consumers       │
│                    ↓                     │
│             Serviço de E-mail            │
└──────────────────────────────────────────┘
```

### Descrição das camadas

**Controllers** — Expõem os endpoints REST para o frontend. Recebem as requisições HTTP, delegam para os Services e devolvem as respostas em formato JSON.

**Services** — Contêm toda a lógica de negócio. Orquestram as operações entre repositórios, validam regras, e publicam eventos no RabbitMQ quando necessário.

**Repositories** — Interfaces do Spring Data JPA responsáveis pelas operações com o banco de dados (consultas, inserções, atualizações e deleções).

**Entidades JPA** — Mapeiam as tabelas do PostgreSQL para objetos Java (`@Entity`).

**DTOs** — Objetos de transferência de dados, usados para desacoplar a camada de apresentação do domínio interno.

**Consumers (Mensageria)** — Ouvem filas do RabbitMQ e executam ações como disparar e-mails ou registrar histórico de eventos.

---

## 🗂️ Principais Classes

### Entidades (Domínio)

**`Cliente`** — Representa um cliente da lanchonete. Armazena nome, e-mail e dados de contato. Mapeado como entidade JPA na tabela de clientes.

**`Pedido`** — Representa um pedido feito por um cliente. Contém a lista de itens, status atual (ex: `RECEBIDO`, `EM_PREPARO`, `PRONTO`, `ENTREGUE`), e a referência ao cliente. A mudança de status é o principal gatilho para os eventos de mensageria.

### Repositórios

**`ClienteRepository`** — Interface Spring Data JPA para operações CRUD de clientes.

**`PedidoRepository`** — Interface Spring Data JPA para operações CRUD de pedidos, incluindo possíveis consultas por status ou cliente.

### Services

**`ClienteService`** — Gerencia o ciclo de vida dos clientes: criação, atualização, listagem e remoção.

**`PedidoService`** — Núcleo da lógica de negócio. Cria pedidos, atualiza status e publica eventos no RabbitMQ após cada mudança de estado.

**`EmailService`** — Responsável pelo envio de e-mails transacionais via Spring Mail. Recebe os dados de notificação e chama o servidor SMTP configurado (Mailtrap ou Ethereal).

### Mensageria

**`NotificacaoConsumer`** — Consumer RabbitMQ que escuta a fila de notificações. Ao receber uma mensagem, aciona o `EmailService` para disparar o e-mail e registra o evento no histórico.

**`EventoHistorico`** — Registra no banco de dados cada evento processado pela fila (ex: quando o e-mail foi enviado, qual pedido, qual cliente).

### DTOs

**`ClienteDTO`** / **`PedidoDTO`** — Objetos usados para trafegar dados entre o frontend e o backend, evitando expor diretamente as entidades JPA.

---

## 📨 Fluxo de Mensageria

```
1. Cliente faz/atualiza pedido via frontend
         ↓
2. PedidoService salva no banco e publica mensagem no RabbitMQ
         ↓
3. RabbitMQ entrega a mensagem na fila de notificações
         ↓
4. NotificacaoConsumer recebe a mensagem
         ↓
5. EmailService envia e-mail para o cliente
         ↓
6. EventoHistorico registra o processamento no banco
```

---

## 🚀 Como Rodar

### Pré-requisitos
- Docker e Docker Compose instalados
- Java 17+
- Node.js 18+

### 1. Subir a infraestrutura

```bash
docker-compose up -d
```

Isso iniciará o **PostgreSQL** (porta `5433`) e o **RabbitMQ** (porta `5672`, painel em `15672`).

### 2. Configurar e-mail

Copie o arquivo de configuração local:

```bash
cp backend/lanchonete/src/main/resources/application-local.yml.example \
   backend/lanchonete/src/main/resources/application-local.yml
```

Edite o arquivo e preencha as credenciais SMTP (veja seção abaixo).

### 3. Iniciar o backend

```bash
cd backend/lanchonete
./mvnw spring-boot:run
```

### 4. Iniciar o frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend estará disponível em `http://localhost:5173`.

---

## 📧 Configuração de E-mail

### Mailtrap (recomendado para testes)

1. Acesse o [Mailtrap](https://mailtrap.io) → **Email Testing** → sua Inbox → **SMTP Settings**
2. Preencha no `application-local.yml`:
   ```yaml
   spring:
     mail:
       host: sandbox.smtp.mailtrap.io
       port: 2525
       username: SEU_USERNAME_SMTP
       password: SUA_SENHA_SMTP
   ```
   > ⚠️ Use o **username/password SMTP**, não o API Token.

**Alternativa via script (Windows):**
```powershell
.\scripts\set-mailtrap-env.ps1 -Username "seu_user_smtp" -Password "sua_senha_smtp"
```

---

## ✅ Status do Projeto

### Core Backend
- [x] Entidades JPA
- [x] Repositories
- [x] DTOs
- [x] Services
- [x] Controllers REST
- [x] CRUD Clientes
- [x] CRUD Pedidos

### Mensageria
- [x] Consumer de notificação
- [x] Histórico de eventos
- [x] Retry queue
- [x] Dead Letter Queue (DLQ)
- [x] Tratamento avançado de erros
- [x] Idempotência

### Frontend
- [x] React + Vite
- [x] Axios
- [x] Tela de Clientes
- [x] Tela de Pedidos
- [x] Tela da Cozinha
- [x] Integração com API

### E-mails
- [x] Mailtrap (SMTP)
- [x] Ethereal (SMTP alternativo)
- [x] Spring Mail configurado
- [x] Serviço de e-mail implementado
- [x] Consumer de notificação integrado

---

## 📁 Estrutura de Pastas

```
LanchaFacil/
├── backend/
│   └── lanchonete/          # Projeto Spring Boot
│       └── src/main/java/
│           └── com/lanchonete/
│               ├── controller/   # Endpoints REST
│               ├── service/      # Lógica de negócio
│               ├── repository/   # Acesso ao banco
│               ├── model/        # Entidades JPA
│               ├── dto/          # Objetos de transferência
│               └── messaging/    # Consumers RabbitMQ
├── frontend/                # Projeto React + Vite
├── scripts/                 # Scripts utilitários
└── docker-compose.yml       # Infraestrutura (PostgreSQL + RabbitMQ)
```
