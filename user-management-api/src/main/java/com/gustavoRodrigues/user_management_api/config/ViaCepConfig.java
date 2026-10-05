package com.gustavorodrigues.user_management_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration  
public class ViaCepConfig {
    

    @Bean 
    public RestClient viaCepRestClient(){
        return RestClient.builder().baseUrl("https://viacep.com.br/ws").build();
    }
}
