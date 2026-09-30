package com.gustavorodrigues.user_management_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavorodrigues.user_management_api.dto.CreateUserDto;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.services.UserServices;

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
    public ResponseEntity<User> createUser(CreateUserDto dto){
        return ResponseEntity.ok(userServices.createCommonUser(dto));
    }   


    /// List
     

}
