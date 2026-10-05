package com.gustavorodrigues.user_management_api.services;

import java.util.List;
import java.util.UUID;


import org.springframework.stereotype.Service;


import com.gustavorodrigues.user_management_api.Exceptions.AddresNotFoundException;
import com.gustavorodrigues.user_management_api.Exceptions.InvalidCepException;
import com.gustavorodrigues.user_management_api.client.viacep.ViaCepCliente;
import com.gustavorodrigues.user_management_api.dto.CreateEnderecoDto;
import com.gustavorodrigues.user_management_api.dto.EnderecoResponseDto;
import com.gustavorodrigues.user_management_api.dto.viacep.ViaCepResponse;
import com.gustavorodrigues.user_management_api.model.Address;
import com.gustavorodrigues.user_management_api.model.User;
import com.gustavorodrigues.user_management_api.repository.AddressRepository;

import jakarta.transaction.Transactional;

@Service
public class AddresService {

    private final AddressRepository addressRepository;
    private final ViaCepCliente viaCepCliente;

    public AddresService(AddressRepository addressRepository, ViaCepCliente viaCepCliente) {
        this.addressRepository = addressRepository;
        this.viaCepCliente = viaCepCliente;
    }

    @Transactional
    public Address createAddres(CreateEnderecoDto dto, User user) {

        ViaCepResponse viaCep = buscarCep(dto.cep());

        Address address = new Address();

        address.setCep(viaCep.cep());
        address.setStreet(viaCep.logradouro());
        address.setNumber(dto.numero());
        address.setComplement(dto.complemento());
        address.setState(viaCep.uf());
        address.setCity(viaCep.localidade());
        address.setNeighborhood(viaCep.bairro());
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

        var address = findById(id);

        ViaCepResponse viaCep = buscarCep(dto.cep());

        address.setCep(viaCep.cep());
        address.setStreet(viaCep.logradouro());
        address.setNumber(dto.numero());
        address.setComplement(dto.complemento());
        address.setState(viaCep.uf());
        address.setCity(viaCep.localidade());
        address.setNeighborhood(viaCep.bairro());
        address.setMain(dto.principal());

        return address;
    }

    public EnderecoResponseDto fetchById(UUID id) {
        return toResponse(findById(id));
    }

    // @Transactional
    // public Address deleteAddres(UUID id) {
    //     var ad = findById(id);
    //     ad.setActive(false);
    //     return addressRepository.save(ad);
    // }

    private Address findById(UUID id) {
        Address ad = addressRepository.findById(id)
                .orElseThrow(() -> new AddresNotFoundException("Endereço não encontrado!"));
        return ad;
    }

    public Address deactivate(UUID id) {
        Address ad = findById(id);
        ad.setActive(false);
        return addressRepository.save(ad);
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

    public ViaCepResponse buscarCep(String cep) {

        String cepNormal = normCep(cep);

        if (cepNormal.length() != 8) {
            throw new InvalidCepException("Cep inválido, o cep deve possuir 8 digitos!");
        }

        ViaCepResponse response = viaCepCliente.fetchCEP(cepNormal);

        if (Boolean.TRUE.equals(response.erro())) {
            throw new InvalidCepException("Cep inválido");
        }

        return response;
    }

    private String normCep(String cep) {
        if (cep == null) {
            throw new InvalidCepException("CEP inválido");
        }

        return cep.replaceAll("\\D", "");
    }

}
