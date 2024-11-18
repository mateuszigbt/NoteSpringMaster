package com.noteapp.exception.auth;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(CredentialsErrorType errorType) {
        super("Invalid credentials: " + errorType.getMessage());
    }
}