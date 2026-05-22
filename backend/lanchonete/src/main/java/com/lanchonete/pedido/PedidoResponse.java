package com.lanchonete.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponse(
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
    public static PedidoResponse fromEntity(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getCliente().getEmail(),
                pedido.getItem(),
                pedido.getObservacao(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getCriadoEm(),
                pedido.getAtualizadoEm()
        );
    }
}
