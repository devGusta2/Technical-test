package com.gustavorodrigues.user_management_api.Exceptions;

public class InvalidCepException extends RuntimeException {
    public InvalidCepException(String message){
        super(message);
    }
}
