package com.lanchonete.notificacao;

import com.lanchonete.config.RabbitConfig;
import com.lanchonete.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitConfig.FILA_NOTIFICACAO)
    public void consumirAtualizacaoPedido(NotificacaoEvento evento) {
        log.info("Consumindo evento do pedido {} com status {}", evento.pedidoId(), evento.status());
        emailService.enviarAtualizacaoPedido(evento);
    }
}
