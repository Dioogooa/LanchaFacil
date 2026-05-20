package com.lanchonete.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pedidos.exchange";

    public static final String FILA_PEDIDOS = "fila.pedidos";
    public static final String FILA_NOTIFICACAO = "fila.notificacao";
    public static final String FILA_HISTORICO = "fila.historico";

    public static final String RK_PEDIDOS = "pedido.criado";
    public static final String RK_NOTIFICACAO = "pedido.status";
    public static final String RK_HISTORICO = "pedido.log";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue filaPedidos() {
        return QueueBuilder.durable(FILA_PEDIDOS).build();
    }

    @Bean
    public Queue filaNotificacao() {
        return QueueBuilder.durable(FILA_NOTIFICACAO).build();
    }

    @Bean
    public Queue filaHistorico() {
        return QueueBuilder.durable(FILA_HISTORICO).build();
    }

    @Bean
    public Binding bindingPedidos() {
        return BindingBuilder
                .bind(filaPedidos())
                .to(exchange())
                .with(RK_PEDIDOS);
    }

    @Bean
    public Binding bindingNotificacao() {
        return BindingBuilder
                .bind(filaNotificacao())
                .to(exchange())
                .with(RK_NOTIFICACAO);
    }

    @Bean
    public Binding bindingHistorico() {
        return BindingBuilder
                .bind(filaHistorico())
                .to(exchange())
                .with(RK_HISTORICO);
    }
}