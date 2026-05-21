package com.lanchonete.event;

import com.lanchonete.common.enums.PedidoStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoCriadoEvent extends BaseEvent {

    private Long pedidoId;
    private String clienteNome;
    private String clienteEmail;
    private PedidoStatus status;

    public PedidoCriadoEvent() {
        super("PedidoCriado");
    }
}