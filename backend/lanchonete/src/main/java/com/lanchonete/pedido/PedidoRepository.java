package com.lanchonete.pedido;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByCriadoEmDesc();

    List<Pedido> findByStatusInOrderByAtualizadoEmAsc(List<StatusPedido> statuses);
}
