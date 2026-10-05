package com.gustavorodrigues.user_management_api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserDto(
        @NotBlank String name,
        @Email String email,
        String password,
        @NotBlank String phone,
        List<@Valid UpdateEnderecoDto> endereco) {
}
