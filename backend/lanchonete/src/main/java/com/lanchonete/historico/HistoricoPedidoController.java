package com.lanchonete.historico;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historico")
@RequiredArgsConstructor
public class HistoricoPedidoController {

    private final HistoricoPedidoService historicoPedidoService;

    @GetMapping
    public List<HistoricoPedidoResponse> listarRecentes() {
        return historicoPedidoService.listarRecentes();
    }
}
