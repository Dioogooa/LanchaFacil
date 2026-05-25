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

/**
 * Remove filas antigas (sem DLX) antes do Spring declarar a topologia nova.
 * RabbitMQ nao permite alterar argumentos de filas ja existentes (PRECONDITION_FAILED).
 */
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
        boolean reset = env.getProperty("app.rabbitmq.reset-topology", Boolean.class, true);
        if (!reset) {
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
            log.info("Topologia RabbitMQ resetada (filas antigas removidas para permitir DLX/retry/DLQ)");
        } catch (IOException | TimeoutException exception) {
            log.warn(
                    "Nao foi possivel resetar filas RabbitMQ antes do startup: {}. "
                            + "Pare o Spring, apague as filas no RabbitMQ Management e suba de novo.",
                    exception.getMessage()
            );
        }
    }

    private void removerFila(Channel channel, String fila) throws IOException {
        try {
            channel.queueDelete(fila, false, false);
            log.debug("Fila removida: {}", fila);
        } catch (IOException exception) {
            if (exception.getMessage() != null && exception.getMessage().contains("404")) {
                return;
            }
            log.debug("Fila {} nao existia ou ja foi removida", fila);
        }
    }
}
