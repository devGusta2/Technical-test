package com.gustavorodrigues.user_management_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEnderecoDto(
    @NotBlank String cep,
    @NotBlank String rua,
    @NotBlank String numero,
    String complemento,
    @NotBlank String estado,
    @NotBlank String cidade,
    @NotBlank String bairro,
    @NotNull boolean principal
)
{}
