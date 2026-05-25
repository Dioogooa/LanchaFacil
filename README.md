
## Status

Core Backend:
- ✅ Entidades JPA
- ✅ Repositories
- ✅ DTOs
- ✅ Services
- ✅ Controllers REST
- ✅ CRUD clientes
- ✅ CRUD pedidos

Mensageria:
- ✅ Consumer de notificacao
- ✅ Historico de eventos
- ✅ Consumer de pedido criado (`fila.pedidos`)
- ✅ Retry queue (DLX + TTL por fila)
- ✅ DLQ (`fila.*.dlq`)
- ✅ Tratamento avancado de erro (retry centralizado + email relanca excecao)
- ✅ Idempotencia (`eventId` + tabela `eventos_processados`)

Front-end:
- ✅ React Vite
- ✅ Axios
- ✅ Tela clientes
- ✅ Tela pedidos
- ✅ Tela cozinha
- ✅ Integracao API

Emails:
- ✅ Mailtrap
- ✅ Ethereal
- ✅ Spring Mail
- ✅ Servico de email
- ✅ Consumer de notificacao

## RabbitMQ — filas retry e DLQ

Ao subir o backend, devem existir **9 filas**: `fila.pedidos`, `fila.notificacao`, `fila.historico` e, para cada uma, `.retry` e `.dlq`, mais o exchange `pedidos.dlx`.

### Erro `PRECONDITION_FAILED` / `inequivalent arg x-dead-letter-exchange`

As filas foram criadas **antes** da topologia com DLX. O RabbitMQ **nao permite** mudar argumentos de fila existente.

**Solucao automatica (padrao):** `app.rabbitmq.reset-topology: true` em `application.yml` apaga as filas conhecidas **antes** do Spring declarar a topologia nova. Reinicie o backend com o RabbitMQ ja rodando.

**Solucao manual:**

```powershell
.\scripts\reset-rabbit-queues.ps1
```

Ou no RabbitMQ Management (http://localhost:15672) apague as filas `fila.pedidos`, `fila.notificacao` e `fila.historico`, depois reinicie o Spring.

### Limitacoes conhecidas

- REST sem `Idempotency-Key` (dois POST = dois pedidos).
- Publicacao sem transactional outbox.
- `NotificacaoProducer` envia em duas filas com dois `convertAndSend` (mesmo `eventId`, nao atomico).

