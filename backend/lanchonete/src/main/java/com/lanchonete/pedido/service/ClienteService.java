package com.lanchonete.pedido.service;

import com.lanchonete.pedido.dto.ClienteRequestDTO;
import com.lanchonete.pedido.dto.ClienteResponseDTO;
import com.lanchonete.pedido.entity.Cliente;
import com.lanchonete.pedido.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());

        Cliente Salvo = clienteRepository.save(cliente);
        return toResponse(Salvo);
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return toResponse(cliente);
    }

    public List<ClienteResponseDTO> listar() {
    return clienteRepository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }
    
    private ClienteResponseDTO toResponse(Cliente cliente) {
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setTelefone(cliente.getTelefone());
        return response;
    }
    
}
