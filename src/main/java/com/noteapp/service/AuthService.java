package com.noteapp.service;

import com.noteapp.dto.auth.UserCredentials;
import com.noteapp.exception.auth.CredentialsErrorType;
import com.noteapp.entity.User;
import com.noteapp.entity.auth.Role;
import com.noteapp.exception.auth.InvalidCredentialsException;
import com.noteapp.exception.auth.UserAlreadyExistsException;
import com.noteapp.exception.auth.UserNotFoundException;
import com.noteapp.repository.UserRepository;
import com.noteapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs a new AuthService with the specified UserRepository, PasswordEncoder, and JwtTokenProvider.
     *
     * @param userRepository The repository for accessing user data.
     * @param passwordEncoder The encoder for encoding user passwords.
     * @param jwtTokenProvider The provider for generating JWT tokens.
     */
    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Performs user login.
     *
     * @param email The email of the user attempting to log in.
     * @param password The password provided by the user.
     * @return The JWT token generated upon successful login.
     * @throws UserNotFoundException if no user with the specified email is found.
     * @throws InvalidCredentialsException if the provided password is incorrect.
     */
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException(CredentialsErrorType.INVALID_PASSWORD);
        }

        return jwtTokenProvider.generateToken(email);
    }

    /**
     * Registers a new user.
     *
     * @param userDto The DTO containing the user registration details.
     * @return The newly registered user.
     * @throws UserAlreadyExistsException if a user with the specified email already exists.
     */
    public User register(UserCredentials userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException(userDto.getEmail());
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        user.setRoles(roles);

        return userRepository.save(user);
    }
}