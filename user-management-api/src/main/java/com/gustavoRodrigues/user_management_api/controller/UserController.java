package com.gustavorodrigues.user_management_api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavorodrigues.user_management_api.dto.CreateUserDto;
import com.gustavorodrigues.user_management_api.dto.UserResponseDto;
import com.gustavorodrigues.user_management_api.services.UserServices;

import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("/users")
public class UserController {
    
    private final UserServices userServices;
    

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }
    //cria r novo usuario
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public UserResponseDto createUser(@RequestBody  CreateUserDto dto){
        return userServices.createCommonUser(dto);
    }   


    /// List
     

}
