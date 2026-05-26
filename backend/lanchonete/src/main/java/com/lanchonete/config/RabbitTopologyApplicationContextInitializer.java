package com.lanchonete.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RabbitTopologyApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(RabbitTopologyApplicationContextInitializer.class);

    private static final List<String> FILAS_PARA_REMOVER = List.of(
            RabbitConfig.FILA_PEDIDOS,
            RabbitConfig.FILA_NOTIFICACAO,
            RabbitConfig.FILA_HISTORICO,
            RabbitConfig.FILA_PEDIDOS_RETRY,
            RabbitConfig.FILA_NOTIFICACAO_RETRY,
            RabbitConfig.FILA_HISTORICO_RETRY,
            RabbitConfig.FILA_PEDIDOS_DLQ,
            RabbitConfig.FILA_NOTIFICACAO_DLQ,
            RabbitConfig.FILA_HISTORICO_DLQ
    );

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment env = applicationContext.getEnvironment();
        if (!env.getProperty("app.rabbitmq.reset-topology", Boolean.class, true)) {
            return;
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.getProperty("spring.rabbitmq.host", "localhost"));
        factory.setPort(env.getProperty("spring.rabbitmq.port", Integer.class, 5672));
        factory.setUsername(env.getProperty("spring.rabbitmq.username", "guest"));
        factory.setPassword(env.getProperty("spring.rabbitmq.password", "guest"));

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            for (String fila : FILAS_PARA_REMOVER) {
                removerFila(channel, fila);
            }
            log.info("Topologia RabbitMQ resetada antes do startup");
        } catch (IOException | TimeoutException exception) {
            log.warn("Nao foi possivel resetar filas RabbitMQ: {}", exception.getMessage());
        }
    }

    private void removerFila(Channel channel, String fila) throws IOException {
        try {
            channel.queueDelete(fila, false, false);
        } catch (IOException ignored) {
            // fila inexistente
        }
    }
}
