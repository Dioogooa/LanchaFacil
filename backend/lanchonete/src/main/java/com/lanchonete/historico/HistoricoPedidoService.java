package com.lanchonete.historico;

import com.lanchonete.notificacao.NotificacaoEvento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricoPedidoService {

    private final HistoricoPedidoRepository historicoPedidoRepository;

    @Transactional
    public void registrar(NotificacaoEvento evento) {
        HistoricoPedido historicoPedido = new HistoricoPedido();
        historicoPedido.setPedidoId(evento.pedidoId());
        historicoPedido.setClienteEmail(evento.clienteEmail());
        historicoPedido.setStatus(evento.status());
        historicoPedido.setMensagem(evento.mensagem());
        historicoPedidoRepository.save(historicoPedido);
    }

    @Transactional(readOnly = true)
    public List<HistoricoPedidoResponse> listarRecentes() {
        return historicoPedidoRepository.findTop20ByOrderByCriadoEmDesc()
                .stream()
                .map(HistoricoPedidoResponse::fromEntity)
                .toList();
    }
}
