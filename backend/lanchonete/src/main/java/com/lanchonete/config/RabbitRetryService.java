package com.lanchonete.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitRetryService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitMqProperties;

    public boolean excedeuMaximoTentativas(Message message) {
        return contarTentativas(message) >= rabbitMqProperties.maxRetries();
    }

    public int contarTentativas(Message message) {
        Object deaths = message.getMessageProperties().getHeaders().get("x-death");
        if (!(deaths instanceof List<?> deathList) || deathList.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Object entry : deathList) {
            if (entry instanceof Map<?, ?> death) {
                Object count = death.get("count");
                if (count instanceof Long longCount) {
                    total += longCount.intValue();
                } else if (count instanceof Integer intCount) {
                    total += intCount;
                }
            }
        }
        return total;
    }

    public void enviarParaDlq(Message message, String dlqRoutingKey) {
        log.warn(
                "Enviando mensagem para DLQ (rk={}, tentativas={})",
                dlqRoutingKey,
                contarTentativas(message)
        );
        rabbitTemplate.send(RabbitConfig.DLX_EXCHANGE, dlqRoutingKey, message);
    }
}
