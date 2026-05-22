package com.lanchonete.historico;

import com.lanchonete.pedido.StatusPedido;

import java.time.LocalDateTime;

public record HistoricoPedidoResponse(
        Long id,
        Long pedidoId,
        String clienteEmail,
        StatusPedido status,
        String mensagem,
        LocalDateTime criadoEm
) {
    public static HistoricoPedidoResponse fromEntity(HistoricoPedido historicoPedido) {
        return new HistoricoPedidoResponse(
                historicoPedido.getId(),
                historicoPedido.getPedidoId(),
                historicoPedido.getClienteEmail(),
                historicoPedido.getStatus(),
                historicoPedido.getMensagem(),
                historicoPedido.getCriadoEm()
        );
    }
}
