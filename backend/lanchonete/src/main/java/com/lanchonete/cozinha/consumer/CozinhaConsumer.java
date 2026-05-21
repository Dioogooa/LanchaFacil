package com.lanchonete.cozinha.consumer;

import com.lanchonete.event.PedidoCriadoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CozinhaConsumer {

    @RabbitListener(queues = "fila.pedidos")
    public void consumirPedido(PedidoCriadoEvent event) {

        System.out.println("=================================");
        System.out.println("NOVO PEDIDO RECEBIDO NA COZINHA");
        System.out.println("Pedido ID: " + event.getPedidoId());
        System.out.println("Cliente: " + event.getClienteNome());
        System.out.println("Status: " + event.getStatus());
        System.out.println("=================================");
    }

}