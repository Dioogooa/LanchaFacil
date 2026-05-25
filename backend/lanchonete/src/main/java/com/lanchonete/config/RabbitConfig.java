package com.lanchonete.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pedidos.exchange";
    public static final String DLX_EXCHANGE = "pedidos.dlx";

    public static final String FILA_PEDIDOS = "fila.pedidos";
    public static final String FILA_NOTIFICACAO = "fila.notificacao";
    public static final String FILA_HISTORICO = "fila.historico";

    public static final String FILA_PEDIDOS_RETRY = "fila.pedidos.retry";
    public static final String FILA_NOTIFICACAO_RETRY = "fila.notificacao.retry";
    public static final String FILA_HISTORICO_RETRY = "fila.historico.retry";

    public static final String FILA_PEDIDOS_DLQ = "fila.pedidos.dlq";
    public static final String FILA_NOTIFICACAO_DLQ = "fila.notificacao.dlq";
    public static final String FILA_HISTORICO_DLQ = "fila.historico.dlq";

    public static final String RK_PEDIDOS = "pedido.criado";
    public static final String RK_NOTIFICACAO = "pedido.status";
    public static final String RK_HISTORICO = "pedido.log";

    public static final String RK_PEDIDOS_RETRY = "pedido.criado.retry";
    public static final String RK_NOTIFICACAO_RETRY = "pedido.status.retry";
    public static final String RK_HISTORICO_RETRY = "pedido.log.retry";

    public static final String RK_PEDIDOS_DLQ = "pedido.criado.dlq";
    public static final String RK_NOTIFICACAO_DLQ = "pedido.status.dlq";
    public static final String RK_HISTORICO_DLQ = "pedido.log.dlq";

    @Value("${app.rabbitmq.retry-ttl-ms:30000}")
    private int retryTtlMs;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue filaPedidos() {
        return QueueBuilder.durable(FILA_PEDIDOS)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(RK_PEDIDOS_RETRY)
                .build();
    }

    @Bean
    public Queue filaNotificacao() {
        return QueueBuilder.durable(FILA_NOTIFICACAO)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(RK_NOTIFICACAO_RETRY)
                .build();
    }

    @Bean
    public Queue filaHistorico() {
        return QueueBuilder.durable(FILA_HISTORICO)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(RK_HISTORICO_RETRY)
                .build();
    }

    @Bean
    public Queue filaPedidosRetry() {
        return QueueBuilder.durable(FILA_PEDIDOS_RETRY)
                .ttl(retryTtlMs)
                .deadLetterExchange(EXCHANGE)
                .deadLetterRoutingKey(RK_PEDIDOS)
                .build();
    }

    @Bean
    public Queue filaNotificacaoRetry() {
        return QueueBuilder.durable(FILA_NOTIFICACAO_RETRY)
                .ttl(retryTtlMs)
                .deadLetterExchange(EXCHANGE)
                .deadLetterRoutingKey(RK_NOTIFICACAO)
                .build();
    }

    @Bean
    public Queue filaHistoricoRetry() {
        return QueueBuilder.durable(FILA_HISTORICO_RETRY)
                .ttl(retryTtlMs)
                .deadLetterExchange(EXCHANGE)
                .deadLetterRoutingKey(RK_HISTORICO)
                .build();
    }

    @Bean
    public Queue filaPedidosDlq() {
        return QueueBuilder.durable(FILA_PEDIDOS_DLQ).build();
    }

    @Bean
    public Queue filaNotificacaoDlq() {
        return QueueBuilder.durable(FILA_NOTIFICACAO_DLQ).build();
    }

    @Bean
    public Queue filaHistoricoDlq() {
        return QueueBuilder.durable(FILA_HISTORICO_DLQ).build();
    }

    @Bean
    public Binding bindingPedidos() {
        return BindingBuilder.bind(filaPedidos()).to(exchange()).with(RK_PEDIDOS);
    }

    @Bean
    public Binding bindingNotificacao() {
        return BindingBuilder.bind(filaNotificacao()).to(exchange()).with(RK_NOTIFICACAO);
    }

    @Bean
    public Binding bindingHistorico() {
        return BindingBuilder.bind(filaHistorico()).to(exchange()).with(RK_HISTORICO);
    }

    @Bean
    public Binding bindingPedidosRetry() {
        return BindingBuilder.bind(filaPedidosRetry()).to(dlxExchange()).with(RK_PEDIDOS_RETRY);
    }

    @Bean
    public Binding bindingNotificacaoRetry() {
        return BindingBuilder.bind(filaNotificacaoRetry()).to(dlxExchange()).with(RK_NOTIFICACAO_RETRY);
    }

    @Bean
    public Binding bindingHistoricoRetry() {
        return BindingBuilder.bind(filaHistoricoRetry()).to(dlxExchange()).with(RK_HISTORICO_RETRY);
    }

    @Bean
    public Binding bindingPedidosDlq() {
        return BindingBuilder.bind(filaPedidosDlq()).to(dlxExchange()).with(RK_PEDIDOS_DLQ);
    }

    @Bean
    public Binding bindingNotificacaoDlq() {
        return BindingBuilder.bind(filaNotificacaoDlq()).to(dlxExchange()).with(RK_NOTIFICACAO_DLQ);
    }

    @Bean
    public Binding bindingHistoricoDlq() {
        return BindingBuilder.bind(filaHistoricoDlq()).to(dlxExchange()).with(RK_HISTORICO_DLQ);
    }
}
