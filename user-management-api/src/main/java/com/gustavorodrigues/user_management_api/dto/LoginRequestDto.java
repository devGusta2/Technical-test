package com.gustavorodrigues.user_management_api.dto;

public record LoginRequestDto(
    String email,
    String password
){}
