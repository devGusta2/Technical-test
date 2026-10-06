package com.gustavorodrigues.user_management_api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavorodrigues.user_management_api.dto.CreateEnderecoDto;
import com.gustavorodrigues.user_management_api.dto.EnderecoResponseDto;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.services.AddresService;
import com.gustavorodrigues.user_management_api.services.UserServices;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/endereco")
public class EnderecoController {
    private final AddresService addresService;
    private final UserServices userServices;

    public EnderecoController(AddresService addresService, UserServices userServices) {
        this.addresService = addresService;
        this.userServices = userServices;
    }

    @GetMapping("/{id}")
    @PreAuthorize("""
@authorizationServices.isAdmin(authentication) or @authorizationServices.isOwnerOfAddress(#id, authentication)
            """)
    public EnderecoResponseDto findById(@PathVariable UUID id) {
        return addresService.fetchById(id);
    }

    @PostMapping("/{userId}")
    @PreAuthorize("""
            @authorizationServices.isAdmin(authentication) or @authorizationServices.isOwnerOfUser(#userId, authentication)
            """)
    public EnderecoResponseDto create(@PathVariable UUID userId, @Valid @RequestBody CreateEnderecoDto dto) {
        User user = userServices.findById(userId);
        return addresService.toResponse(
                addresService.createAddres(dto, user));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("""
            @authorizationServices.isAdmin(authentication)
            or @authorizationServices.isOwnerOfAddress(#id, authentication)
            """)
    public EnderecoResponseDto update(@PathVariable UUID id,@Valid @RequestBody  CreateEnderecoDto dto) {
        return addresService.toResponse(
                addresService.updateAddress(dto, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("""
            @authorizationServices.isAdmin(authentication)
            or @authorizationServices.isOwnerOfAddress(#id, authentication)
            """)
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        addresService.deactivate(id);
        return ResponseEntity.noContent().build();
    }


    
}
