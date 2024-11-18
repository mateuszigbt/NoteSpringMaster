package com.noteapp.controller;

import com.noteapp.dto.auth.UserCredentials;
import com.noteapp.dto.auth.JwtResponse;
import com.noteapp.entity.User;
import com.noteapp.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class handling authentication-related HTTP requests.
 * Endpoints for user sign-in and sign-up are provided.
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor for AuthController, injecting an instance of AuthService.
     * @param authService The service responsible for authentication operations.
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint for user sign-in.
     * @param credentials UserCredentials object containing email and password.
     * @return ResponseEntity containing a JWT token upon successful sign-in.
     */
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> login(@RequestBody UserCredentials credentials) {
        String token = authService.login(credentials.getEmail(), credentials.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    /**
     * Endpoint for user sign-up.
     * @param userCredentials UserCredentials object containing email and password.
     * @return ResponseEntity containing a newly created User upon successful sign-up.
     */
    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody UserCredentials userCredentials) {
        User user = authService.register(userCredentials);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}