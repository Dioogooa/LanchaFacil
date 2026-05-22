package com.lanchonete.pedido.service;

import com.lanchonete.pedido.dto.PedidoRequestDTO;
import com.lanchonete.pedido.dto.PedidoResponseDTO;
import com.lanchonete.pedido.entity.Item;
import com.lanchonete.pedido.entity.Pedido;
import com.lanchonete.pedido.repository.PedidoRepository;
import com.lanchonete.pedido.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        var clienete = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        
        Pedido pedido = new Pedido();
        pedido.setCliente(clienete);

        List<Item> itens = dto.getItens().stream().map(itemDTO -> {
            Item item = new Item();
            item.setNome(itemDTO.getNome());
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPreco(itemDTO.getPreco());
            item.setPedido(pedido);
            return item;
        }).collect(Collectors.toList());

        pedido.setItens(itens);
        Pedido salvo = pedidoRepository.save(pedido);
        return toResponse(salvo);
    }

    public List<PedidoResponseDTO> listar() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deletar(long id) {
        pedidoRepository.deleteById(id);
    }

    public PedidoResponseDTO buscarPorId(Long id) {
    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    return toResponse(pedido);
    }

    private PedidoResponseDTO toResponse(Pedido pedido) {
        PedidoResponseDTO response = new PedidoResponseDTO();
        response.setId(pedido.getId());
        response.setClienteNome(pedido.getCliente().getNome());
        response.setStatus(pedido.getStatus());
        response.setCriadoEm(pedido.getCriadoEm());
        response.setItens(pedido.getItens().stream().map(Item::getNome).collect(Collectors.toList()));
        return response;
        
    }
    
}
