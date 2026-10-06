package com.gustavorodrigues.user_management_api.dto;

public record LoginResponseDto(
    String accessToken,
    Long expiresIn,
    String role
){}
