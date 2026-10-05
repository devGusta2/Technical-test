package com.gustavorodrigues.user_management_api.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavorodrigues.user_management_api.dto.CreateEnderecoDto;
import com.gustavorodrigues.user_management_api.dto.CreateUserDto;
import com.gustavorodrigues.user_management_api.dto.UserDto;
import com.gustavorodrigues.user_management_api.dto.UserResponseDto;
import com.gustavorodrigues.user_management_api.dto.UpdateUserDto;
import com.gustavorodrigues.user_management_api.dto.UpdateEnderecoDto;
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
            long p = dto.endereco().stream().filter(CreateEnderecoDto::principal).count(); // quantidade de endereços principais
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

    public List<UserResponseDto> listAll(){
        List<User> list = userRepository.findAll();
        return list.stream()
        .map(
            this::toResponse).toList();
    }

    @Transactional
    public UserResponseDto updateUser(UUID id, UpdateUserDto dto) {
        User user = findById(id);

        if (dto.email() != null && !dto.email().equals(user.getEmail())
                && userRepository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado!");
        }

        if (dto.name() != null) user.setName(dto.name());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.phone() != null) user.setPhone(dto.phone());
        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(encoder.encode(dto.password()));
        }

        if (dto.endereco() != null) {
            long mainCount = dto.endereco().stream().filter(UpdateEnderecoDto::principal).count();
            if (mainCount > 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "O usuário pode ter apenas um endereço principal!");
            }

            boolean assigningMain = mainCount == 1;
            if (assigningMain) {
                user.getAddress().forEach(address -> address.setMain(false));
            }

            for (UpdateEnderecoDto addressDto : dto.endereco()) {
                if (addressDto.id() == null) {
                    addresService.createAddres(new CreateEnderecoDto(
                            addressDto.cep(), addressDto.numero(), addressDto.complemento(), addressDto.principal()), user);
                } else {
                    var address = user.getAddress().stream()
                            .filter(current -> current.getId().equals(addressDto.id()))
                            .findFirst()
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Endereço não encontrado para este usuário!"));
                    addresService.updateAddress(new CreateEnderecoDto(
                            addressDto.cep(), addressDto.numero(), addressDto.complemento(), addressDto.principal()), address.getId());
                }
            }
        }

        return toResponse(userRepository.save(user));
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

    public UserResponseDto deactivate(UUID id){
        var user = findById(id);
        user.setActive(false);
        return toResponse(userRepository.save(user));
    }

    public User findById(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
