package com.gustavorodrigues.user_management_api.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavorodrigues.user_management_api.dto.LoginRequestDto;
import com.gustavorodrigues.user_management_api.dto.LoginResponseDto;
import com.gustavorodrigues.user_management_api.services.AuthService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController 
@RequestMapping("/auth") 
public class TokenController {

    private final AuthService authService;

    public TokenController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest){

        return authService.login(loginRequest);
    }
}
