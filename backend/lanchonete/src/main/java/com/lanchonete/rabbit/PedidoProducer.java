package com.lanchonete.rabbit;

import com.lanchonete.config.RabbitConfig;
import com.lanchonete.event.PedidoCriadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarPedidoCriado(PedidoCriadoEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.RK_PEDIDOS,
                event
        );

        System.out.println("EVENTO ENVIADO: " + event.getEventId());
    }

}