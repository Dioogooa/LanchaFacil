package com.lanchonete.pedido.messaging;

import com.lanchonete.pedido.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoCriadoEvento(
        UUID eventId,
        Long id,
        Long clienteId,
        String clienteNome,
        String clienteEmail,
        String item,
        String observacao,
        BigDecimal valorTotal,
        StatusPedido status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
