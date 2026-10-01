package com.gustavorodrigues.user_management_api.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavorodrigues.user_management_api.dto.CreateEnderecoDto;
import com.gustavorodrigues.user_management_api.dto.CreateUserDto;
import com.gustavorodrigues.user_management_api.dto.UserDto;
import com.gustavorodrigues.user_management_api.dto.UserResponseDto;
import com.gustavorodrigues.user_management_api.enums.RoleEnum;
import com.gustavorodrigues.user_management_api.model.Role;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServices {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final RoleService roleService;
    private final AddresService addresService;

    public UserServices(UserRepository userRepository, BCryptPasswordEncoder encoder, RoleService roleService,
            AddresService addresService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleService = roleService;
        this.addresService = addresService;
    }

    public Optional<User> fetchByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Credenciais inválidas!"));
    }

    public User createUser(CreateUserDto dto, Role role) {
        return saveUser(
                dto.name(),
                dto.email(),
                dto.phone(),
                dto.password(),
                role);
    }

    public User createAdminUser(UserDto dto, Role role) {
        return saveUser(
                dto.name(),
                dto.email(),
                dto.phone(),
                dto.password(),
                role);
    }

    @Transactional
    public UserResponseDto createCommonUser(CreateUserDto dto) {
        if (dto.endereco() != null) {
            long p = dto.endereco().stream().filter(CreateEnderecoDto::principal).count(); // quantidade de endereços
                                                                                           // principais
            if (p > 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "O usuário pode ter apenas um endereço principal!");
            }
        }
        Role role = roleService.findOrCreate("USER");

        User user = saveUser(
                dto.name(),
                dto.email(),
                dto.phone(),
                dto.password(),
                role);

        if (dto.endereco() != null) {
            dto.endereco().forEach(e -> addresService.createAddres(e, user));
        }
        return toResponse(user);
    }

    private User saveUser(
            String name,
            String email,
            String phone,
            String password,
            Role role) {

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(encoder.encode(password));
        user.setActive(true);
        user.setRoles(Set.of(role));
        user.setRole(RoleEnum.valueOf(role.getName()));

        return userRepository.save(user);
    }

    private UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isActive(),
                user.getAddress()
                        .stream()
                        .map(addresService::toResponse)
                        .toList());
    }

}
