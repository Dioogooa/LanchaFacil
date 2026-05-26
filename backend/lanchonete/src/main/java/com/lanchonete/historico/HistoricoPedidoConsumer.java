package com.lanchonete.historico;

import com.lanchonete.config.RabbitConfig;
import com.lanchonete.config.RabbitConsumerExecutor;
import com.lanchonete.notificacao.NotificacaoEvento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoricoPedidoConsumer {

    private final HistoricoPedidoService historicoPedidoService;
    private final RabbitConsumerExecutor rabbitConsumerExecutor;

    @RabbitListener(queues = RabbitConfig.FILA_HISTORICO)
    public void registrarEvento(NotificacaoEvento evento, Message message) {
        log.info("Registrando historico do pedido {}", evento.pedidoId());
        rabbitConsumerExecutor.executar(message, RabbitConfig.RK_HISTORICO_DLQ, () ->
                historicoPedidoService.registrar(evento)
        );
    }
}
