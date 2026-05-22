package com.lanchonete.pedido.dto;

import com.lanchonete.common.enums.PedidoStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

public class PedidoResponseDTO {

    private Long id;
    private String clienteNome;
    private List<String> itens;
    private PedidoStatus status;
    private LocalDateTime criadoEm;

}
