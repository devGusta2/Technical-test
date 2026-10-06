package com.gustavoRodrigues.user_management_api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.gustavorodrigues.user_management_api.Exceptions.UserNotFoundException;

import com.gustavorodrigues.user_management_api.dto.CreateUserDto;
import com.gustavorodrigues.user_management_api.model.Role;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.UserRepository;
import com.gustavorodrigues.user_management_api.services.AddresService;
import com.gustavorodrigues.user_management_api.services.RoleService;
import com.gustavorodrigues.user_management_api.services.UserServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private UserServices userServices;

        @Mock
        private RoleService roleService;

        @Mock
        private AddresService addresService;

        @Mock
        private BCryptPasswordEncoder encoder;

        @Test
        void criarUsuarioDadosValidoss() {
                CreateUserDto dto = new CreateUserDto(
                                "Gustavo",
                                "gustavo@email.com",
                                "123456", // password
                                "11999999999", // phone
                                null);

                Role role = new Role();
                role.setName("USER");

                User user = new User();
                user.setName("Gustavo");
                user.setEmail("gustavo@email.com");
                user.setPhone("11999999999");

                when(roleService.findOrCreate("USER"))
                                .thenReturn(role);

                when(encoder.encode("123456"))
                                .thenReturn("senha-hash");

                when(userRepository.save(any(User.class)))
                                .thenReturn(user);

                var result = userServices.createCommonUser(dto);

                assertEquals("Gustavo", result.name());
                assertEquals("gustavo@email.com", result.email());
                assertEquals("11999999999", result.phone());

                verify(roleService).findOrCreate("USER");
                verify(encoder).encode("123456");
                verify(userRepository).save(any(User.class));
                verify(userRepository).save(argThat(savedUser -> savedUser.getName().equals("Gustavo") &&
                                savedUser.getEmail().equals("gustavo@email.com") &&
                                savedUser.getPhone().equals("11999999999") &&
                                savedUser.isActive()

                ));
        }

        @Test
        void deveCriptografarSenha() {
                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

                UserServices service = new UserServices(
                                userRepository,
                                bcrypt,
                                roleService,
                                addresService);

                CreateUserDto dto = new CreateUserDto(
                                "Gustavo",
                                "gustavo@email.com",
                                "123456", // password
                                "11999999999", // phone
                                null);

                Role role = new Role();

                role.setName("USER");

                when(roleService.findOrCreate("USER"))
                                .thenReturn(role);

                when(userRepository.save(any(User.class)))
                                .thenAnswer(inv -> inv.getArgument(0));

                service.createCommonUser(dto);

                var captura = ArgumentCaptor.forClass(User.class);

                verify(userRepository).save(captura.capture());

                User user = captura.getValue();

                assertNotEquals("123456", user.getPassword());
                assertTrue(bcrypt.matches("123456", user.getPassword()));
        }

        @Test
        void deveBuscarUsuarioExistente() {
                UUID id = UUID.randomUUID();

                User user = new User();
                user.setId(id);
                user.setName("Gustavo");
                user.setEmail("gustavo@gmail.com");
                user.setActive(true);

                when(userRepository.findByIdAndActiveTrue(id))
                                .thenReturn(Optional.of(user));

                User resultd = userServices.findById(id);

                assertEquals(id, resultd.getId());
                assertEquals("Gustavo", resultd.getName());
                assertEquals("gustavo@gmail.com", resultd.getEmail());

                verify(userRepository).findByIdAndActiveTrue(id);
        }

        @Test
        void execaoQuandoNaoExisteUsuario() {
                UUID id = UUID.randomUUID();

                when(userRepository.findByIdAndActiveTrue(id))
                                .thenReturn(Optional.empty());

                UserNotFoundException exception = assertThrows(
                                UserNotFoundException.class,
                                () -> userServices.findById(id));

                assertEquals("Usuário não encontrado!", exception.getMessage());

                verify(userRepository).findByIdAndActiveTrue(id);
        }

        @Test
        void deveDeletarLogicamente() {
                UUID id = UUID.randomUUID();

                User user = new User();
                user.setId(id);
                user.setActive(true);

                when(userRepository.findByIdAndActiveTrue(id))
                                .thenReturn(Optional.of(user));

                when(userRepository.save(user))
                                .thenReturn(user);

                userServices.deactivate(id);

                assertFalse(user.isActive());

                verify(userRepository).findByIdAndActiveTrue(id);
                verify(userRepository).save(user);
        }

}
