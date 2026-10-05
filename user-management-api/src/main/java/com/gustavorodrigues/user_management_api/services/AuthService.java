package com.gustavorodrigues.user_management_api.services;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.gustavorodrigues.user_management_api.dto.LoginRequestDto;
import com.gustavorodrigues.user_management_api.dto.LoginResponseDto;
import com.gustavorodrigues.user_management_api.model.Role;

@Service
public class AuthService {

    private final UserServices userServices;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthService(UserServices userServices, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userServices = userServices;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public ResponseEntity<LoginResponseDto> login(LoginRequestDto dto) {
        var user = userServices.fetchByEmail(dto.email());
        if (!user.get().isActive()) {
            throw new BadCredentialsException("Credenciais inválidas!");
        }
        if (user.isEmpty()) {
            throw new BadCredentialsException("Credenciais inválidas!");
        }

        if (!passwordEncoder.matches(dto.password(), user.get().getPassword())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
        var expiresIn = 1000L;
        var now = Instant.now();
        var scopes = user.get().getRoles().stream().map(Role::getName).collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("ApiTecnicalTest")
                .subject(user.get().getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims));

        return ResponseEntity.ok(new LoginResponseDto(jwtValue.getTokenValue(), expiresIn));

    }

}
