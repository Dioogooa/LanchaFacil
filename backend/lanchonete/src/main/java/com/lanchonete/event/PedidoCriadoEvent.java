package com.lanchonete.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoCriadoEvent extends BaseEvent {

    private Long pedidoId;
    private String clienteNome;
    private String clienteEmail;
    private String status;

    public PedidoCriadoEvent() {
        super("PedidoCriado");
    }
}