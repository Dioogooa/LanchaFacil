package com.lanchonete.pedido;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PedidoRequest(
        @NotNull(message = "Cliente e obrigatorio")
        Long clienteId,
        @NotBlank(message = "Item e obrigatorio")
        String item,
        String observacao,
        @NotNull(message = "Valor total e obrigatorio")
        @DecimalMin(value = "0.01", message = "Valor total deve ser maior que zero")
        BigDecimal valorTotal
) {
}
