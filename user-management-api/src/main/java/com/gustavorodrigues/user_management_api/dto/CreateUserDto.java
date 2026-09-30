package com.gustavorodrigues.user_management_api.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CreateUserDto(
    @NotNull String name,
    @NotNull @Email String email,
    @NotNull String password,
    @NotNull String phone,
    List<CreateEnderecoDto> endereco
) {}
