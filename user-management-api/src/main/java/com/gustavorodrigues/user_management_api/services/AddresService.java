package com.gustavorodrigues.user_management_api.services;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavorodrigues.user_management_api.dto.CreateEnderecoDto;
import com.gustavorodrigues.user_management_api.dto.EnderecoResponseDto;
import com.gustavorodrigues.user_management_api.model.Address;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.AddressRepository;

import jakarta.transaction.Transactional;

@Service
public class AddresService {

    private final AddressRepository addressRepository;

    public AddresService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional
    public Address createAddres(CreateEnderecoDto dto, User user) {
        Address address = new Address();
        address.setCep(dto.cep());
        address.setStreet(dto.rua());
        address.setNumber(dto.numero());
        address.setComplement(dto.complemento());
        address.setState(dto.estado());
        address.setCity(dto.cidade());
        address.setNeighborhood(dto.bairro());
        address.setMain(dto.principal());
        address.setUser(user);
        user.getAddress().add(address);
        return addressRepository.save(address);
    }

    public List<EnderecoResponseDto> listAddress() {
        return addressRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public Address updateAddress(CreateEnderecoDto dto, UUID id) {
        var ad = findById(id);
        ad.setCep(dto.cep());
        ad.setStreet(dto.rua());
        ad.setNumber(dto.numero());
        ad.setComplement(dto.complemento());
        ad.setState(dto.estado());
        ad.setCity(dto.cidade());
        ad.setNeighborhood(dto.bairro());
        ad.setMain(dto.principal());

        return ad;
    }

    public EnderecoResponseDto fetchById(UUID id) {
        return toResponse(findById(id));
    }

    @Transactional 
    public Address deleteAddres(UUID id) {
        var ad = findById(id);
        ad.setActive(false);
        return addressRepository.save(ad);
    }

    private Address findById(UUID id) {
        Address ad = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado!"));
        return ad;
    }

    public EnderecoResponseDto toResponse(Address address) {
        return new EnderecoResponseDto(
                address.getId(),
                address.getCep(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getState(),
                address.getCity(),
                address.getNeighborhood(),
                address.isMain());
    }


}
