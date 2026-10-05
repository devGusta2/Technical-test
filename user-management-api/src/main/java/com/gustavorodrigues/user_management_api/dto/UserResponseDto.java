package com.gustavorodrigues.user_management_api.dto;

import java.util.List;
import java.util.UUID;

import com.gustavorodrigues.user_management_api.enums.RoleEnum;

public record UserResponseDto(
    UUID id,
    String name,
    String email,
    String phone,
    RoleEnum role,
    Boolean active,
    List<EnderecoResponseDto> enderecos) {
}
