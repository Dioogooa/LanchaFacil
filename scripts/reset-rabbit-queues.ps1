# Remove filas antigas do RabbitMQ (quando reset-topology nao rodou ou o Spring ja estava no ar)
$container = "rabbitmq-lanchonete"
$filas = @(
    "fila.pedidos",
    "fila.notificacao",
    "fila.historico",
    "fila.pedidos.retry",
    "fila.notificacao.retry",
    "fila.historico.retry",
    "fila.pedidos.dlq",
    "fila.notificacao.dlq",
    "fila.historico.dlq"
)

foreach ($fila in $filas) {
    docker exec $container rabbitmqctl delete_queue $fila 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Removida: $fila"
    }
}

Write-Host "Pronto. Reinicie o backend Spring para recriar as 9 filas com DLX/retry/DLQ."
