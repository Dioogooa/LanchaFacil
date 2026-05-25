package com.lanchonete.pedido.messaging;

import com.lanchonete.common.ResourceNotFoundException;
import com.lanchonete.messaging.ConsumerType;
import com.lanchonete.messaging.EventoProcessadoService;
import com.lanchonete.pedido.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoCriadoService {

    private final PedidoRepository pedidoRepository;
    private final EventoProcessadoService eventoProcessadoService;

    @Transactional
    public void processar(PedidoCriadoEvento evento) {
        if (eventoProcessadoService.jaProcessado(evento.eventId(), ConsumerType.PEDIDO_CRIADO)) {
            log.info("Evento {} ja processado em PEDIDO_CRIADO", evento.eventId());
            return;
        }

        pedidoRepository.findById(evento.id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido " + evento.id() + " nao encontrado para processamento assincrono"
                ));

        log.info(
                "Pedido {} ingerido assincronamente (eventId={}, item={})",
                evento.id(),
                evento.eventId(),
                evento.item()
        );

        eventoProcessadoService.marcarProcessado(evento.eventId(), ConsumerType.PEDIDO_CRIADO);
    }
}
