package com.gustavorodrigues.user_management_api.client.viacep;

import com.gustavorodrigues.user_management_api.dto.viacep.ViaCepResponse;

public interface ViaCepCliente {

    ViaCepResponse fetchCEP(String cep);
    
}
