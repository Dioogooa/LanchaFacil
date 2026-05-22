package com.lanchonete.historico;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoPedidoRepository extends JpaRepository<HistoricoPedido, Long> {

    List<HistoricoPedido> findTop20ByOrderByCriadoEmDesc();
}
