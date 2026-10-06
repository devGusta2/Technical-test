package com.gustavorodrigues.user_management_api.exceptions;

public class EmailAlreadExistsEception extends RuntimeException {
    public EmailAlreadExistsEception(String message){
        super(message);
    }
}
