package com.gustavoRodrigues.user_management_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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


import com.gustavorodrigues.user_management_api.dto.CreateEnderecoDto;
import com.gustavorodrigues.user_management_api.model.Address;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.AddressRepository;
import com.gustavorodrigues.user_management_api.services.AddresService;
import com.gustavorodrigues.user_management_api.services.UserServices;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {
    

    @Mock 
    private AddressRepository addressRepository;

    @Mock 
    private UserServices userServices;

    @InjectMocks 
    private AddresService addresService;


    @Test 
    void cadastrarEndereco(){
        CreateEnderecoDto dto = new CreateEnderecoDto("111111111",
         "Rua endereço", 
         "122", 
         " ", 
         "SP", 
         "SP", 
         "Jaridm São Paulo",
        true);
        
        User user = new User();

        when(addressRepository.save(any(Address.class)))
            .thenAnswer(end -> end.getArgument(0));

        Address ad = addresService.createAddres(dto, user);

        assertEquals("111111111", ad.getCep());
        assertEquals("Rua endereço", ad.getStreet());
        assertEquals(" ", ad.getComplement());
        assertEquals("SP", ad.getState());
        assertEquals("SP", ad.getCity());
        assertEquals("Jaridm São Paulo", ad.getNeighborhood());
        assertTrue(ad.isMain());

        verify(addressRepository).save(ad);

    }


    @Test 
    void deveDesativarEndereco(){
        UUID id = UUID.randomUUID();
        Address ad = new Address();
        ad.setId(id);
        ad.setActive(true);

        when(addressRepository.findById(id))
            .thenReturn(Optional.of(ad));

        addresService.deactivate(id);
        assertFalse(ad.isActive());
        verify(addressRepository).findById(id);
        verify(addressRepository).save(ad);

    }

}
