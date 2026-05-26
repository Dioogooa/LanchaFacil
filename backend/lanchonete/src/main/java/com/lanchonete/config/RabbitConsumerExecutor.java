package com.lanchonete.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitConsumerExecutor {

    private final RabbitRetryService rabbitRetryService;

    public void executar(Message message, String dlqRoutingKey, Runnable processamento) {
        if (rabbitRetryService.excedeuMaximoTentativas(message)) {
            rabbitRetryService.enviarParaDlq(message, dlqRoutingKey);
            return;
        }
        processamento.run();
    }
}
