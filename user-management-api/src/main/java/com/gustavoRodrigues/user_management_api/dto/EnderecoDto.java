package com.gustavorodrigues.user_management_api.dto;

public record EnderecoDto(
    String cep,
    String rua,
    String numero,
    String complemento,
    String estado,
    String cidade,
    String bairro
)
{}
