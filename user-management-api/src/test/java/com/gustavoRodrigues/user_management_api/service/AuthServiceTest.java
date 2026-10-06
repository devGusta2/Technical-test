package com.gustavorodrigues.user_management_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.gustavorodrigues.user_management_api.dto.LoginRequestDto;
import com.gustavorodrigues.user_management_api.dto.LoginResponseDto;
import com.gustavorodrigues.user_management_api.enums.RoleEnum;
import com.gustavorodrigues.user_management_api.model.Role;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.services.AuthService;
import com.gustavorodrigues.user_management_api.services.UserServices;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserServices userServices;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginValido() {

        UUID id = UUID.randomUUID();

        LoginRequestDto dto = new LoginRequestDto(
                "gustavo@email.com",
                "123456");

        Role role = new Role();
        role.setName("USER");

        User user = new User();
        user.setId(id);
        user.setEmail("gustavo@email.com");
        user.setPassword("123");
        user.setActive(true);
        user.setRole(RoleEnum.USER);
        user.setRoles(Set.of(role));

        Jwt jwt = mock(Jwt.class);

        when(userServices.fetchByEmail("gustavo@email.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "123456",
                user.getPassword()))
                .thenReturn(true);

        when(jwt.getTokenValue())
                .thenReturn("meu-jwt-aqui");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        ResponseEntity<LoginResponseDto> response = authService.login(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("meu-jwt-aqui", response.getBody().accessToken());
        assertEquals(1000L, response.getBody().expiresIn());

        verify(userServices).fetchByEmail("gustavo@email.com");
        verify(passwordEncoder).matches("123456", user.getPassword());
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void loginInvalido() {
        UUID id = UUID.randomUUID();

        LoginRequestDto dto = new LoginRequestDto(
                "gustavo@gmail.com",
                "123456");

        Role role = new Role();
        role.setName("USER");

        User user = new User();
        user.setId(id);
        user.setEmail("gustavo@gmail.com");
        user.setPassword("123456");
        user.setActive(true);
        user.setRole(RoleEnum.USER);
        user.setRoles(Set.of(role));

        when(userServices.fetchByEmail(user.getEmail()))
            .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", user.getPassword()))
            .thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> authService.login(dto));

        verify(userServices).fetchByEmail("gustavo@gmail.com");
        verify(passwordEncoder).matches("123456", user.getPassword());
    }

    @Test
    void loginDeveFalharQuandoUsuarioNaoExiste() {
        LoginRequestDto dto = new LoginRequestDto("ausente@email.com", "senha");
        when(userServices.fetchByEmail(dto.email())).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> authService.login(dto));
        verify(userServices).fetchByEmail(dto.email());
        org.mockito.Mockito.verifyNoInteractions(passwordEncoder, jwtEncoder);
    }

    @Test
    void loginDeveFalharQuandoUsuarioEstaInativo() {
        LoginRequestDto dto = new LoginRequestDto("inativo@email.com", "senha");
        User user = new User();
        user.setActive(false);
        when(userServices.fetchByEmail(dto.email())).thenReturn(Optional.of(user));
        assertThrows(BadCredentialsException.class, () -> authService.login(dto));
        org.mockito.Mockito.verifyNoInteractions(passwordEncoder, jwtEncoder);
    }

}
