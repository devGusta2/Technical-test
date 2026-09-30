package com.gustavorodrigues.user_management_api.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavorodrigues.user_management_api.controller.UserController;
import com.gustavorodrigues.user_management_api.dto.UserDto;

import com.gustavorodrigues.user_management_api.enums.RoleEnum;
import com.gustavorodrigues.user_management_api.model.Role;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.UserRepository;

@Service
public class UserServices {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServices(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public Optional<User> fetchByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credenciais inválidas!"));
    }

    public User createUser(UserDto dto, Role role) {
        User u = new User();

        u.setName(dto.name());
        u.setEmail(dto.email());
        u.setPhone(dto.phone());
        u.setPassword(encoder.encode(dto.password()));
        u.setActive(true);
        u.setRoles(Set.of(role));
        u.setRole(RoleEnum.valueOf(role.getName()));

        return userRepository.save(u);
    }

    // public User createUser(UserDto dto){
    // var pas = encoder.encode(dto.password());

    // User u = new User(dto.email(), pas, dto.name(), dto.phone(), Role.USER);

    // return u;
    // }

}
