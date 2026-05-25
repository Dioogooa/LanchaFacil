package com.lanchonete.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventoProcessadoService {

    private final EventoProcessadoRepository eventoProcessadoRepository;

    @Transactional(readOnly = true)
    public boolean jaProcessado(UUID eventId, ConsumerType consumerType) {
        return eventoProcessadoRepository.existsByEventIdAndConsumerType(eventId, consumerType);
    }

    @Transactional
    public void marcarProcessado(UUID eventId, ConsumerType consumerType) {
        if (jaProcessado(eventId, consumerType)) {
            return;
        }
        EventoProcessado eventoProcessado = new EventoProcessado();
        eventoProcessado.setEventId(eventId);
        eventoProcessado.setConsumerType(consumerType);
        try {
            eventoProcessadoRepository.save(eventoProcessado);
        } catch (DataIntegrityViolationException ignored) {
            // concorrencia: outra entrega ja marcou o evento
        }
    }
}
