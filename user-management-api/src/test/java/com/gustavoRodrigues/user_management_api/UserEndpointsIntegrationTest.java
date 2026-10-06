package com.gustavoRodrigues.user_management_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.gustavorodrigues.user_management_api.client.viacep.ViaCepCliente;
import com.gustavorodrigues.user_management_api.dto.viacep.ViaCepResponse;
import com.gustavorodrigues.user_management_api.enums.RoleEnum;
import com.gustavorodrigues.user_management_api.model.Role;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.AddressRepository;
import com.gustavorodrigues.user_management_api.repository.RoleRepository;
import com.gustavorodrigues.user_management_api.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;

    @MockitoBean
    private ViaCepCliente viaCepCliente;

    private User user;
    private String userToken;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    return roleRepository.save(newRole);
                });

        user = new User();
        user.setName("Usuário de Integração");
        user.setEmail("integration-" + UUID.randomUUID() + "@example.com");
        user.setPhone("11999999999");
        user.setPassword("hash");
        user.setActive(true);
        user.setRole(RoleEnum.USER);
        user.setRoles(new HashSet<>(java.util.Set.of(role)));

        userRepository.save(user);

        userToken = token(user.getId(), "USER");
    }

    @Test
    void deveRetornar401QuandoNaoInformarToken() throws Exception {
        mockMvc.perform(
                get("/api/v1/users/list")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoUsuarioTentaListarUsuarios() throws Exception {
        mockMvc.perform(
                get("/api/v1/users/list")
                        .header("Authorization", bearer(userToken))
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAdminListarUsuarios() throws Exception {
        String adminToken = token(UUID.randomUUID(), "ADMIN");

        mockMvc.perform(
                get("/api/v1/users/list")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc")
                        .header("Authorization", bearer(adminToken))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void devePermitirUsuarioConsultarProprioCadastro() throws Exception {
        mockMvc.perform(
                get("/api/v1/users/" + user.getId())
                        .header("Authorization", bearer(userToken))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void deveNegarUsuarioConsultarCadastroDeOutroUsuario() throws Exception {
        User otherUser = criarOutroUsuario();

        mockMvc.perform(
                get("/api/v1/users/" + otherUser.getId())
                        .header("Authorization", bearer(userToken))
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void deveCriarUsuarioComoAdmin() throws Exception {
        String adminId = UUID.randomUUID().toString();
        String adminToken = token(UUID.fromString(adminId), "ADMIN");
        String email = "novo-" + UUID.randomUUID() + "@example.com";

        String body = """
                {
                    "name": "Novo Usuário",
                    "email": "%s",
                    "password": "123456",
                    "phone": "11999999999",
                    "endereco": null
                }
                """.formatted(email);

        mockMvc.perform(
                post("/api/v1/users/create")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(email))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.createdBy").value(adminId))
        .andExpect(jsonPath("$.updatedAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedBy").value(adminId));

        User created = userRepository.findByEmail(email)
                .orElseThrow();

        assertNotNull(created.getCreatedAt());
        assertEquals(UUID.fromString(adminId), created.getCreatedBy());
    }

    @Test
    void deveRetornar409QuandoEmailJaExiste() throws Exception {
        String adminToken = token(UUID.randomUUID(), "ADMIN");

        String body = """
                {
                    "name": "Outro Usuário",
                    "email": "%s",
                    "password": "123456",
                    "phone": "11999999999",
                    "endereco": null
                }
                """.formatted(user.getEmail());

        mockMvc.perform(
                post("/api/v1/users/create")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deveRetornar400QuandoDadosDoUsuarioSaoInvalidos() throws Exception {
        String adminToken = token(UUID.randomUUID(), "ADMIN");

        String body = """
                {
                    "name": "",
                    "email": "email-invalido",
                    "password": "",
                    "phone": ""
                }
                """;

        mockMvc.perform(
                post("/api/v1/users/create")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void devePermitirUsuarioAtualizarProprioCadastro() throws Exception {
        String body = """
                {
                    "name": "Nome Atualizado",
                    "email": null,
                    "password": null,
                    "phone": "11888888888",
                    "endereco": null
                }
                """;

        mockMvc.perform(
                patch("/api/v1/users/" + user.getId())
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Nome Atualizado"))
        .andExpect(jsonPath("$.phone").value("11888888888"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedBy").value(user.getId().toString()));

        User updated = userRepository.findById(user.getId())
                .orElseThrow();

        assertNotNull(updated.getUpdatedAt());
        assertEquals(user.getId(), updated.getUpdatedBy());
    }

    @Test
    void deveDesativarUsuarioComoAdmin() throws Exception {
        String adminToken = token(UUID.randomUUID(), "ADMIN");

        mockMvc.perform(
                patch("/api/v1/users/" + user.getId() + "/deactivate")
                        .header("Authorization", bearer(adminToken))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));

        User deactivated = userRepository.findById(user.getId())
                .orElseThrow();

        assertEquals(false, deactivated.isActive());
    }

    @Test
    void deveRetornar404AoConsultarUsuarioDesativado() throws Exception {
        String adminToken = token(UUID.randomUUID(), "ADMIN");

        mockMvc.perform(
                patch("/api/v1/users/" + user.getId() + "/deactivate")
                        .header("Authorization", bearer(adminToken))
        )
        .andExpect(status().isOk());

        mockMvc.perform(
                get("/api/v1/users/" + user.getId())
                        .header("Authorization", bearer(adminToken))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveCriarEnderecoComCepValido() throws Exception {
        when(viaCepCliente.fetchCEP("01001000"))
                .thenReturn(new ViaCepResponse(
                        "01001-000",
                        "Praça da Sé",
                        "lado ímpar",
                        "Sé",
                        "São Paulo",
                        "SP",
                        false
                ));

        String body = """
                {
                    "cep": "01001-000",
                    "numero": "12",
                    "complemento": "Casa",
                    "principal": true
                }
                """;

        mockMvc.perform(
                post("/api/v1/endereco/" + user.getId())
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cep").value("01001-000"))
        .andExpect(jsonPath("$.rua").value("Praça da Sé"))
        .andExpect(jsonPath("$.principal").value(true));

        assertEquals(
                1,
                addressRepository.findByUserIdAndIsActiveTrue(user.getId()).size()
        );
    }

    @Test
    void deveRetornar400QuandoCepNaoExiste() throws Exception {
        when(viaCepCliente.fetchCEP("99999999"))
                .thenReturn(new ViaCepResponse(
                        "99999-999",
                        null,
                        null,
                        null,
                        null,
                        null,
                        true
                ));

        String body = """
                {
                    "cep": "99999-999",
                    "numero": "12",
                    "principal": false
                }
                """;

        mockMvc.perform(
                post("/api/v1/endereco/" + user.getId())
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void devePermitirUsuarioConsultarProprioEndereco() throws Exception {
        UUID addressId = criarEndereco();

        mockMvc.perform(
                get("/api/v1/endereco/" + addressId)
                        .header("Authorization", bearer(userToken))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(addressId.toString()));
    }

    @Test
    void deveNegarUsuarioConsultarEnderecoDeOutroUsuario() throws Exception {
        User otherUser = criarOutroUsuario();
        UUID addressId = criarEnderecoPara(otherUser);

        mockMvc.perform(
                get("/api/v1/endereco/" + addressId)
                        .header("Authorization", bearer(userToken))
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void deveDesativarEnderecoComoUsuario() throws Exception {
        UUID addressId = criarEndereco();

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/v1/endereco/" + addressId)
                        .header("Authorization", bearer(userToken))
        )
        .andExpect(status().isNoContent());

        assertEquals(
                0,
                addressRepository.findByUserIdAndIsActiveTrue(user.getId()).size()
        );
    }

    private User criarOutroUsuario() {
        Role role = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    return roleRepository.save(newRole);
                });

        User otherUser = new User();
        otherUser.setName("Outro Usuário");
        otherUser.setEmail("other-" + UUID.randomUUID() + "@example.com");
        otherUser.setPhone("11988888888");
        otherUser.setPassword("hash");
        otherUser.setActive(true);
        otherUser.setRole(RoleEnum.USER);
        otherUser.setRoles(new HashSet<>(java.util.Set.of(role)));

        return userRepository.save(otherUser);
    }

    private UUID criarEndereco() {
        when(viaCepCliente.fetchCEP("01001000"))
                .thenReturn(new ViaCepResponse(
                        "01001-000",
                        "Praça da Sé",
                        "lado ímpar",
                        "Sé",
                        "São Paulo",
                        "SP",
                        false
                ));

        String body = """
                {
                    "cep": "01001-000",
                    "numero": "10",
                    "complemento": "Casa",
                    "principal": true
                }
                """;

        try {
            mockMvc.perform(
                    post("/api/v1/endereco/" + user.getId())
                            .header("Authorization", bearer(userToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
            )
            .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return addressRepository
                .findByUserIdAndIsActiveTrue(user.getId())
                .get(0)
                .getId();
    }

    private UUID criarEnderecoPara(User targetUser) {
        when(viaCepCliente.fetchCEP("01001000"))
                .thenReturn(new ViaCepResponse(
                        "01001-000",
                        "Praça da Sé",
                        "lado ímpar",
                        "Sé",
                        "São Paulo",
                        "SP",
                        false
                ));

        String body = """
                {
                    "cep": "01001-000",
                    "numero": "20",
                    "complemento": "Casa",
                    "principal": true
                }
                """;

        String targetToken = token(targetUser.getId(), "USER");

        try {
            mockMvc.perform(
                    post("/api/v1/endereco/" + targetUser.getId())
                            .header("Authorization", bearer(targetToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
            )
            .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return addressRepository
                .findByUserIdAndIsActiveTrue(targetUser.getId())
                .get(0)
                .getId();
    }

    private String token(UUID subject, String scope) {
        Instant now = Instant.now();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwtClaimsSet.builder()
                                .issuer("integration-test")
                                .subject(subject.toString())
                                .issuedAt(now)
                                .expiresAt(now.plusSeconds(300))
                                .claim("scope", scope)
                                .build()
                )
        ).getTokenValue();
    }

    private String bearer(String jwt) {
        return "Bearer " + jwt;
    }
}
