package com.lanchonete.cliente;

import com.lanchonete.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return ClienteResponse.fromEntity(obterEntidade(id));
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        validarEmailDuplicado(request.email(), null);
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        return ClienteResponse.fromEntity(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest request) {
        Cliente cliente = obterEntidade(id);
        validarEmailDuplicado(request.email(), id);
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        return ClienteResponse.fromEntity(clienteRepository.save(cliente));
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = obterEntidade(id);
        clienteRepository.delete(cliente);
    }

    public Cliente obterEntidade(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));
    }

    private void validarEmailDuplicado(String email, Long id) {
        boolean duplicado = id == null
                ? clienteRepository.existsByEmail(email)
                : clienteRepository.existsByEmailAndIdNot(email, id);
        if (duplicado) {
            throw new IllegalArgumentException("Ja existe cliente com este email");
        }
    }
}
