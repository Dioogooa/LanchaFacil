package com.lanchonete.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank(message = "Nome e obrigatorio")
        String nome,
        @Email(message = "Email invalido")
        @NotBlank(message = "Email e obrigatorio")
        String email,
        @NotBlank(message = "Telefone e obrigatorio")
        String telefone
) {
}
