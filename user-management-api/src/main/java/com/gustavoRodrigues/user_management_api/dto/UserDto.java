package com.gustavorodrigues.user_management_api.dto;

public record UserDto(
    String nome,
    String email,
    String password,
    String telefone
){}
