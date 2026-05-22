package com.lanchonete.pedido.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClienteResponseDTO {
    
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    
}
