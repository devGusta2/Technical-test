package com.gustavoRodrigues.user_management_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.gustavorodrigues.user_management_api.client.viacep.ViaCepCLientImpl;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

class ViaCepClienteTest {

    @Test
    void consultaCepUsandoRespostaHttpMockada() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        ViaCepCLientImpl client = new ViaCepCLientImpl(builder.baseUrl("https://viacep.test/ws").build());

        server.expect(requestTo("https://viacep.test/ws/01001000/json"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"cep":"01001-000","logradouro":"Praça da Sé","complemento":"lado ímpar",
                         "bairro":"Sé","localidade":"São Paulo","uf":"SP","erro":false}
                        """, MediaType.APPLICATION_JSON));

        var response = client.fetchCEP("01001000");

        assertEquals("01001-000", response.cep());
        assertEquals("São Paulo", response.localidade());
        assertFalse(response.erro());
        server.verify();
    }
}
