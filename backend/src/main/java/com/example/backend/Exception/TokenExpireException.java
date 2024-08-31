package com.example.backend.Exception;

public class TokenExpireException extends RuntimeException {

    public TokenExpireException(String message) {
        super(message);
    }
}
