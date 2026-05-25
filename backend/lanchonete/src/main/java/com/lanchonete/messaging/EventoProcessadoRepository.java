package com.lanchonete.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventoProcessadoRepository extends JpaRepository<EventoProcessado, Long> {

    boolean existsByEventIdAndConsumerType(UUID eventId, ConsumerType consumerType);
}
