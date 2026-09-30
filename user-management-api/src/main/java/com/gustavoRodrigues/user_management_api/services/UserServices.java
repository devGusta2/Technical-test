package com.gustavorodrigues.user_management_api.services;



import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gustavorodrigues.user_management_api.dto.UserDto;

import com.gustavorodrigues.user_management_api.enums.RoleEnum;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.UserRepository;

@Service 
public class UserServices {

    // private final UserRepository userRepository;
    // private final BCryptPasswordEncoder encoder;
    // private final Addr
    // public UserServices(UserRepository userRepository, BCryptPasswordEncoder encoder){
    //     this.userRepository = userRepository;
    //     this.encoder = encoder;
    // }

    // public User createUser(UserDto dto){
    //     var pas = encoder.encode(dto.password());

    //     User u = new User(dto.email(), pas, dto.name(), dto.phone(), Role.USER);

    //     return u;
    // }

}
