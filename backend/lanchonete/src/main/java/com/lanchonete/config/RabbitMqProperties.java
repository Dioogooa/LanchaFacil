package com.lanchonete.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMqProperties(
        int maxRetries,
        int retryTtlMs,
        boolean resetTopology
) {
}
