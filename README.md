
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
- [ ] Retry queue
- [ ] DLQ
- [ ] Tratamento avancado de erro
- [ ] Idempotencia

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

### Mailtrap (SMTP — caminho 1)

1. No Mailtrap: **Email Testing** → sua Inbox → **SMTP Settings** (host `sandbox.smtp.mailtrap.io`, porta `2525`, TLS).
2. Copie `application-local.yml.example` para `application-local.yml` e cole **username** e **password** SMTP (nao use o API token).
3. Reinicie o backend — o Spring carrega `application-local.yml` automaticamente (arquivo no `.gitignore`).

Alternativa sem arquivo local:

```powershell
.\scripts\set-mailtrap-env.ps1 -Username "seu_user_smtp" -Password "sua_senha_smtp"
```

