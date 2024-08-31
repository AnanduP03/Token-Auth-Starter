package com.example.backend.Exception;

public class EmailExistsException extends Exception{
    public EmailExistsException(String message){
        super(message);
    }
}
