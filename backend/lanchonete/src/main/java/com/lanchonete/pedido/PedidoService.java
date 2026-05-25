package com.lanchonete.pedido;

import com.lanchonete.cliente.Cliente;
import com.lanchonete.cliente.ClienteService;
import com.lanchonete.common.ResourceNotFoundException;
import com.lanchonete.notificacao.NotificacaoEvento;
import com.lanchonete.notificacao.NotificacaoProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lanchonete.pedido.messaging.PedidoCriadoEvento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.lanchonete.config.RabbitConfig.EXCHANGE;
import static com.lanchonete.config.RabbitConfig.RK_PEDIDOS;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;
    private final RabbitTemplate rabbitTemplate;
    private final NotificacaoProducer notificacaoProducer;

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return pedidoRepository.findAllByOrderByCriadoEmDesc()
                .stream()
                .map(PedidoResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPainelCozinha() {
        return pedidoRepository.findByStatusInOrderByAtualizadoEmAsc(List.of(
                StatusPedido.RECEBIDO,
                StatusPedido.EM_PREPARO,
                StatusPedido.PRONTO
        )).stream().map(PedidoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return PedidoResponse.fromEntity(obterEntidade(id));
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        Cliente cliente = clienteService.obterEntidade(request.clienteId());
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setItem(request.item());
        pedido.setObservacao(request.observacao() == null || request.observacao().isBlank()
                ? "Sem observacoes"
                : request.observacao());
        pedido.setValorTotal(request.valorTotal());
        pedido.setStatus(StatusPedido.RECEBIDO);
        Pedido salvo = pedidoRepository.save(pedido);

        UUID eventIdPedido = UUID.randomUUID();
        rabbitTemplate.convertAndSend(EXCHANGE, RK_PEDIDOS, toPedidoCriadoEvento(eventIdPedido, salvo));
        publicarNotificacao(salvo, "Pedido recebido com sucesso.");
        return PedidoResponse.fromEntity(salvo);
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, PedidoStatusRequest request) {
        Pedido pedido = obterEntidade(id);
        pedido.setStatus(request.status());
        Pedido atualizado = pedidoRepository.save(pedido);
        publicarNotificacao(atualizado, "Status do pedido atualizado para " + request.status() + ".");
        return PedidoResponse.fromEntity(atualizado);
    }

    private Pedido obterEntidade(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado"));
    }

    private PedidoCriadoEvento toPedidoCriadoEvento(UUID eventId, Pedido pedido) {
        return new PedidoCriadoEvento(
                eventId,
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getCliente().getEmail(),
                pedido.getItem(),
                pedido.getObservacao(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getCriadoEm(),
                pedido.getAtualizadoEm()
        );
    }

    private void publicarNotificacao(Pedido pedido, String mensagem) {
        NotificacaoEvento evento = new NotificacaoEvento(
                UUID.randomUUID(),
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getCliente().getEmail(),
                pedido.getItem(),
                pedido.getStatus(),
                mensagem,
                LocalDateTime.now()
        );
        notificacaoProducer.publicarEvento(evento);
    }
}
