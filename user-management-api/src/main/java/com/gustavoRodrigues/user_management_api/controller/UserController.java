package com.gustavorodrigues.user_management_api.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gustavorodrigues.user_management_api.dto.CreateUserDto;
import com.gustavorodrigues.user_management_api.dto.UpdateUserDto;
import com.gustavorodrigues.user_management_api.dto.UserResponseDto;
import com.gustavorodrigues.user_management_api.services.UserServices;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    // cria r novo usuario
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public UserResponseDto createUser(@RequestBody CreateUserDto dto) {
        return userServices.createCommonUser(dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or #id.toString() == authentication.name")
    public UserResponseDto updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserDto dto) {

        return userServices.updateUser(id, dto);
    }

    /// List


    @GetMapping("/list")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Page<UserResponseDto> listUser(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @PageableDefault (size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        return userServices.listAll(name, email, pageable);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public UserResponseDto deactivate(@PathVariable UUID id) {
        return userServices.deactivate(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or #id.toString() == authentication.name")
    public UserResponseDto findById(@PathVariable UUID id) {
        return userServices.findByid(id);
    }
}
