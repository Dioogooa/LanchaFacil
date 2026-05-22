package com.lanchonete.pedido.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter

public class PedidoRequestDTO {

    private Long clienteId;
    private List<ItemDTO> itens;

    @Getter
    @Setter
    public static class ItemDTO {
        private String nome;
        private Integer quantidade;
        private Double preco;
    }
    
}
