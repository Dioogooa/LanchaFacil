package com.lanchonete.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoStatusEvent extends BaseEvent {

    private Long pedidoId;
    private String status;
    private String mensagem;

    public PedidoStatusEvent() {
        super("PedidoStatus");
    }
}