package com.noteapp.exception.auth;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException() {
        super("User not authenticated.");
    }
}
