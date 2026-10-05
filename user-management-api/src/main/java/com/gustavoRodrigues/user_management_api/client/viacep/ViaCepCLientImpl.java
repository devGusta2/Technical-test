package com.gustavorodrigues.user_management_api.client.viacep;


import org.springframework.web.client.RestClient;

import com.gustavorodrigues.user_management_api.dto.viacep.ViaCepResponse;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class ViaCepCLientImpl implements  ViaCepCliente {
    private final RestClient viaCepRestClient;

    @Override 
    @Cacheable(value = "cep", key = "#cep")
    public ViaCepResponse fetchCEP(String cep){
        return viaCepRestClient.get().uri("/{cep}/json", cep).retrieve().body(ViaCepResponse.class);
    }
}
