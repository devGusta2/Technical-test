package com.gustavorodrigues.user_management_api.Exceptions;

public class EmailAlreadExistsEception extends RuntimeException {
    public EmailAlreadExistsEception(String message){
        super(message);
    }
}
