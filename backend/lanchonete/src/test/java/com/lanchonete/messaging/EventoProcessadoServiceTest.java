package com.lanchonete.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoProcessadoServiceTest {

    @Mock
    private EventoProcessadoRepository eventoProcessadoRepository;

    @InjectMocks
    private EventoProcessadoService eventoProcessadoService;

    @Test
    void deveMarcarEventoQuandoAindaNaoProcessado() {
        UUID eventId = UUID.randomUUID();
        when(eventoProcessadoRepository.existsByEventIdAndConsumerType(eventId, ConsumerType.HISTORICO))
                .thenReturn(false);

        eventoProcessadoService.marcarProcessado(eventId, ConsumerType.HISTORICO);

        verify(eventoProcessadoRepository).save(any(EventoProcessado.class));
    }

    @Test
    void naoDeveSalvarQuandoJaProcessado() {
        UUID eventId = UUID.randomUUID();
        when(eventoProcessadoRepository.existsByEventIdAndConsumerType(eventId, ConsumerType.NOTIFICACAO))
                .thenReturn(true);

        eventoProcessadoService.marcarProcessado(eventId, ConsumerType.NOTIFICACAO);

        verify(eventoProcessadoRepository, never()).save(any());
    }
}
