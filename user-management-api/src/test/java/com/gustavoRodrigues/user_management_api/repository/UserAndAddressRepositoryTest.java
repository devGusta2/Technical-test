package com.gustavorodrigues.user_management_api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.gustavorodrigues.user_management_api.model.Address;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.AddressRepository;
import com.gustavorodrigues.user_management_api.repository.UserRepository;

@DataJpaTest
class UserAndAddressRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired AddressRepository addressRepository;
    @Autowired org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void filtraUsuariosAtivosComPaginacaoEOrdenacao() {
        User ativo = user("Ana Silva", "ana.repository@example.com", true);
        User inativo = user("Ana Inativa", "ana.inativa@example.com", false);
        userRepository.save(ativo);
        userRepository.save(inativo);
        entityManager.flush();

        var page = userRepository.findActiveUsers("ana", "example.com", PageRequest.of(0, 1, Sort.by("name")));

        assertEquals(1, page.getTotalElements());
        assertEquals("Ana Silva", page.getContent().get(0).getName());
        assertTrue(userRepository.existsByEmail("ana.repository@example.com"));
        assertTrue(userRepository.findByEmail("ana.repository@example.com").isPresent());
        assertTrue(userRepository.findByIdAndIsActiveTrue(ativo.getId()).isPresent());
        assertFalse(userRepository.findByIdAndIsActiveTrue(inativo.getId()).isPresent());
        assertEquals(1, userRepository.findByIsActiveTrue(PageRequest.of(0, 10)).getTotalElements());
    }

    @Test
    void retornaSomenteEnderecosAtivosEIdentificaPrincipalAtivoDoUsuario() {
        User user = user("Bruno", "bruno.repository@example.com", true);
        userRepository.save(user);
        Address principal = address(user, true);
        Address inactive = address(user, false);
        inactive.setActive(false);
        addressRepository.save(principal);
        addressRepository.save(inactive);
        entityManager.flush();

        assertEquals(1, addressRepository.findByIsActiveTrue().size());
        assertEquals(1, addressRepository.findByUserIdAndIsActiveTrue(user.getId()).size());
        assertTrue(addressRepository.findByIdAndIsActiveTrue(principal.getId()).isPresent());
        assertFalse(addressRepository.findByIdAndIsActiveTrue(inactive.getId()).isPresent());
        assertEquals(principal.getId(), addressRepository
                .findByUserIdAndMainTrueAndIsActiveTrue(user.getId()).orElseThrow().getId());
    }

    private User user(String name, String email, boolean active) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone("11999999999");
        user.setPassword("hash");
        user.setActive(active);
        user.setRole(com.gustavorodrigues.user_management_api.enums.RoleEnum.USER);
        user.setRoles(new HashSet<>());
        user.setAddress(new HashSet<>());
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private Address address(User user, boolean main) {
        Address address = new Address();
        address.setUser(user);
        address.setMain(main);
        address.setCep("01001000");
        address.setStreet("Rua Teste");
        address.setNumber("10");
        address.setState("SP");
        address.setCity("São Paulo");
        address.setNeighborhood("Sé");
        address.setCreatedAt(LocalDateTime.now());
        return address;
    }
}
