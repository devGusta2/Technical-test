package com.gustavoRodrigues.user_management_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gustavorodrigues.user_management_api.client.viacep.ViaCepCliente;
import com.gustavorodrigues.user_management_api.Exceptions.AddresNotFoundException;
import com.gustavorodrigues.user_management_api.Exceptions.InvalidCepException;
import com.gustavorodrigues.user_management_api.dto.CreateEnderecoDto;
import com.gustavorodrigues.user_management_api.dto.viacep.ViaCepResponse;
import com.gustavorodrigues.user_management_api.model.Address;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.AddressRepository;
import com.gustavorodrigues.user_management_api.services.AddresService;


@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock 
    private ViaCepCliente viaCepCliente;
   

    @InjectMocks
    private AddresService addresService;

    @Test
    void cadastrarEndereco() {

        CreateEnderecoDto dto = new CreateEnderecoDto(
                "11111111",
                "122",
                " ",
                true);

        User user = new User();

        ViaCepResponse viaCepResponse = new ViaCepResponse(
                "11111111",
                "Rua endereço",
                "",
                "Jardim São Paulo",
                "São Paulo",
                "SP",
                false);

        when(viaCepCliente.fetchCEP("11111111"))
                .thenReturn(viaCepResponse);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Address ad = addresService.createAddres(dto, user);

        assertEquals("11111111", ad.getCep());
        assertEquals("Rua endereço", ad.getStreet());
        assertEquals(" ", ad.getComplement());
        assertEquals("SP", ad.getState());
        assertEquals("São Paulo", ad.getCity());
        assertEquals("Jardim São Paulo", ad.getNeighborhood());
        assertTrue(ad.isMain());

        verify(viaCepCliente).fetchCEP("11111111");
        verify(addressRepository).save(ad);
    }

    @Test
    void deveDesativarEndereco() {
        UUID id = UUID.randomUUID();
        Address ad = new Address();
        ad.setId(id);
        ad.setActive(true);

        when(addressRepository.findByIdAndIsActiveTrue(id))
                .thenReturn(Optional.of(ad));

        addresService.deactivate(id);
        assertFalse(ad.isActive());
        verify(addressRepository).findByIdAndIsActiveTrue(id);
        verify(addressRepository).save(ad);

    }

    @Test
    void deveRejeitarCepMalformadoSemConsultarServicoExterno() {
        assertThrows(InvalidCepException.class,
                () -> addresService.buscarCep("12.34"));
        org.mockito.Mockito.verifyNoInteractions(viaCepCliente);
    }

    @Test
    void deveRejeitarCepNaoEncontradoNoViaCep() {
        when(viaCepCliente.fetchCEP("12345678"))
                .thenReturn(new ViaCepResponse("12345678", null, null, null, null, null, true));
        assertThrows(InvalidCepException.class, () -> addresService.buscarCep("12345-678"));
        verify(viaCepCliente).fetchCEP("12345678");
    }

    @Test
    void deveLancarExcecaoParaEnderecoInexistente() {
        UUID id = UUID.randomUUID();
        when(addressRepository.findByIdAndIsActiveTrue(id)).thenReturn(Optional.empty());
        assertThrows(AddresNotFoundException.class, () -> addresService.fetchById(id));
    }

    @Test
    void deveDesmarcarEnderecoPrincipalAnteriorAoCriarOutro() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Address anterior = new Address();
        anterior.setId(UUID.randomUUID());
        anterior.setUser(user);
        anterior.setMain(true);
        when(addressRepository.findByUserIdAndMainTrueAndIsActiveTrue(userId)).thenReturn(Optional.of(anterior));
        when(viaCepCliente.fetchCEP("11111111"))
                .thenReturn(new ViaCepResponse("11111111", "Rua", "", "Bairro", "Cidade", "SP", false));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Address novo = addresService.createAddres(new CreateEnderecoDto("11111111", "10", null, true), user);

        assertFalse(anterior.isMain());
        assertTrue(novo.isMain());
        verify(addressRepository).save(anterior);
    }

    @Test
    void deveAtualizarEnderecoExistente() {
        UUID id = UUID.randomUUID();
        Address address = new Address();
        address.setId(id);
        address.setMain(true);
        User user = new User();
        user.setId(UUID.randomUUID());
        address.setUser(user);
        when(addressRepository.findByIdAndIsActiveTrue(id)).thenReturn(Optional.of(address));
        when(viaCepCliente.fetchCEP("87654321"))
                .thenReturn(new ViaCepResponse("87654321", "Rua Nova", "", "Centro", "Santos", "SP", false));
        when(addressRepository.findByUserIdAndMainTrueAndIsActiveTrue(user.getId())).thenReturn(Optional.of(address));
        when(addressRepository.save(address)).thenReturn(address);

        Address result = addresService.updateAddress(new CreateEnderecoDto("87654-321", "50", "apto", true), id);

        assertEquals("Rua Nova", result.getStreet());
        assertEquals("87654321", result.getCep());
        assertTrue(result.isMain());
        verify(addressRepository).save(address);
    }

}
