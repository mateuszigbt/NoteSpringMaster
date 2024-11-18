package com.noteapp.exception.auth;

public enum CredentialsErrorType {
    EMAIL_NOT_FOUND("email not found"),
    INVALID_PASSWORD("invalid password");

    private final String message;

    CredentialsErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}