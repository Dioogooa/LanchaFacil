package com.lanchonete.pedido.messaging;

import com.lanchonete.config.RabbitConfig;
import com.lanchonete.config.RabbitConsumerExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoCriadoConsumer {

    private final PedidoCriadoService pedidoCriadoService;
    private final RabbitConsumerExecutor rabbitConsumerExecutor;

    @RabbitListener(queues = RabbitConfig.FILA_PEDIDOS)
    public void consumirPedidoCriado(PedidoCriadoEvento evento, Message message) {
        log.info("Consumindo pedido criado {} (eventId={})", evento.id(), evento.eventId());
        rabbitConsumerExecutor.executar(message, RabbitConfig.RK_PEDIDOS_DLQ, () ->
                pedidoCriadoService.processar(evento)
        );
    }
}
