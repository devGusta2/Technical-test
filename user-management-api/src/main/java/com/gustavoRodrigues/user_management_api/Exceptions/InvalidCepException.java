package com.gustavorodrigues.user_management_api.exceptions;

public class InvalidCepException extends RuntimeException {
    public InvalidCepException(String message){
        super(message);
    }
}
