package com.lanchonete.notificacao;

import com.lanchonete.email.EmailService;
import com.lanchonete.messaging.ConsumerType;
import com.lanchonete.messaging.EventoProcessadoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoProcessamentoService {

    private final EmailService emailService;
    private final EventoProcessadoService eventoProcessadoService;

    @Transactional
    public void processar(NotificacaoEvento evento) {
        if (eventoProcessadoService.jaProcessado(evento.eventId(), ConsumerType.NOTIFICACAO)) {
            log.info("Evento {} ja processado em NOTIFICACAO", evento.eventId());
            return;
        }

        emailService.enviarAtualizacaoPedido(evento);
        eventoProcessadoService.marcarProcessado(evento.eventId(), ConsumerType.NOTIFICACAO);
    }
}
