package com.gustavorodrigues.user_management_api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record UpdateEnderecoDto(
        UUID id,
        @NotBlank String cep,
        @NotBlank String numero,
        String complemento,
        boolean principal) {
}
