package com.example.backend.Exception;

public class UsernameExistsException extends Exception{
    public UsernameExistsException(String message){
        super(message);
    }
}
