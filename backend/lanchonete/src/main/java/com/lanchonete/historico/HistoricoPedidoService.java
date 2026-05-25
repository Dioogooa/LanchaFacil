package com.lanchonete.historico;

import com.lanchonete.messaging.ConsumerType;
import com.lanchonete.messaging.EventoProcessadoService;
import com.lanchonete.notificacao.NotificacaoEvento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoricoPedidoService {

    private final HistoricoPedidoRepository historicoPedidoRepository;
    private final EventoProcessadoService eventoProcessadoService;

    @Transactional
    public void registrar(NotificacaoEvento evento) {
        if (eventoProcessadoService.jaProcessado(evento.eventId(), ConsumerType.HISTORICO)) {
            log.info("Evento {} ja processado em HISTORICO", evento.eventId());
            return;
        }

        HistoricoPedido historicoPedido = new HistoricoPedido();
        historicoPedido.setEventId(evento.eventId());
        historicoPedido.setPedidoId(evento.pedidoId());
        historicoPedido.setClienteEmail(evento.clienteEmail());
        historicoPedido.setStatus(evento.status());
        historicoPedido.setMensagem(evento.mensagem());
        try {
            historicoPedidoRepository.save(historicoPedido);
        } catch (DataIntegrityViolationException exception) {
            log.info("Historico duplicado para eventId {}", evento.eventId());
            return;
        }
        eventoProcessadoService.marcarProcessado(evento.eventId(), ConsumerType.HISTORICO);
    }

    @Transactional(readOnly = true)
    public List<HistoricoPedidoResponse> listarRecentes() {
        return historicoPedidoRepository.findTop20ByOrderByCriadoEmDesc()
                .stream()
                .map(HistoricoPedidoResponse::fromEntity)
                .toList();
    }
}
