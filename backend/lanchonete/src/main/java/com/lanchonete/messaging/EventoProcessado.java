package com.lanchonete.messaging;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "eventos_processados",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "consumer_type"})
)
public class EventoProcessado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "consumer_type", nullable = false)
    private ConsumerType consumerType;

    @Column(nullable = false)
    private LocalDateTime processadoEm;

    @PrePersist
    public void prePersist() {
        processadoEm = LocalDateTime.now();
    }
}
