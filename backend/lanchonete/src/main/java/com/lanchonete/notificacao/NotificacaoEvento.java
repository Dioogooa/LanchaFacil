package com.lanchonete.notificacao;

import com.lanchonete.pedido.StatusPedido;

import java.time.LocalDateTime;

public record NotificacaoEvento(
        Long pedidoId,
        Long clienteId,
        String clienteNome,
        String clienteEmail,
        String item,
        StatusPedido status,
        String mensagem,
        LocalDateTime dataEvento
) {
}
