package com.lanchonete.pedido;

import jakarta.validation.constraints.NotNull;

public record PedidoStatusRequest(
        @NotNull(message = "Status e obrigatorio")
        StatusPedido status
) {
}
