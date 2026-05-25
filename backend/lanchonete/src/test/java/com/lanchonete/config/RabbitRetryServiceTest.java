package com.lanchonete.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitRetryServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RabbitMqProperties rabbitMqProperties;

    @InjectMocks
    private RabbitRetryService rabbitRetryService;

    @Test
    void deveContarTentativasAPartirDoHeaderXDeath() {
        when(rabbitMqProperties.maxRetries()).thenReturn(3);

        Message message = new Message("payload".getBytes(), new MessageProperties());
        message.getMessageProperties().setHeader("x-death", List.of(Map.of("count", 2L)));

        assertThat(rabbitRetryService.contarTentativas(message)).isEqualTo(2);
        assertThat(rabbitRetryService.excedeuMaximoTentativas(message)).isFalse();
    }

    @Test
    void deveEnviarParaDlqQuandoExcederMaximo() {
        when(rabbitMqProperties.maxRetries()).thenReturn(2);

        Message message = new Message("payload".getBytes(), new MessageProperties());
        message.getMessageProperties().setHeader("x-death", List.of(Map.of("count", 2L)));

        assertThat(rabbitRetryService.excedeuMaximoTentativas(message)).isTrue();

        rabbitRetryService.enviarParaDlq(message, RabbitConfig.RK_NOTIFICACAO_DLQ);

        verify(rabbitTemplate).send(RabbitConfig.DLX_EXCHANGE, RabbitConfig.RK_NOTIFICACAO_DLQ, message);
    }
}
