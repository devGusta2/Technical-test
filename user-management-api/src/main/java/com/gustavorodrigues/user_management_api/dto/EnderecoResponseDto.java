package com.gustavorodrigues.user_management_api.dto;

import java.util.UUID;

public record EnderecoResponseDto(
        UUID id,
        String cep,
        String rua,
        String numero,
        String complemento,
        String estado,
        String cidade,
        String bairro,
        boolean principal
) {
}
