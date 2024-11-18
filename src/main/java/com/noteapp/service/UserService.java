package com.noteapp.service;

import com.noteapp.dto.auth.UserCredentials;
import com.noteapp.entity.User;
import com.noteapp.exception.auth.UserNotAuthenticatedException;
import com.noteapp.exception.auth.UserNotFoundException;
import com.noteapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserService with the specified UserRepository and PasswordEncoder.
     *
     * @param userRepository The repository for accessing user data.
     * @param passwordEncoder The encoder for encoding user passwords.
     */
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a list of all users.
     *
     * @return A list containing all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return The currently authenticated user.
     * @throws UserNotAuthenticatedException if no user is authenticated.
     */
    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                authentication == null ||
                        !authentication.isAuthenticated() ||
                        !(authentication.getPrincipal() instanceof UserDetails userDetails)
        ) {
            throw new UserNotAuthenticatedException();
        }

        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Retrieves a user by email.
     *
     * @param email The email of the user to retrieve.
     * @return The user with the specified email.
     * @throws UserNotFoundException if no user with the specified email is found.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The user with the specified ID.
     * @throws UserNotFoundException if no user with the specified ID is found.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Deletes a user by ID.
     *
     * @param id The ID of the user to delete.
     * @throws UserNotFoundException if no user with the specified ID is found.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Update the current user's information based on email.
     * @param userCredentials UserCredentials object containing updated user details.
     * @return The updated User object.
     */
    public User updateCurrentUser(UserCredentials userCredentials) {
        User user = getCurrentUser();

        // Update email if provided
        if (userCredentials.getEmail() != null && !userCredentials.getEmail().isEmpty()) {
            user.setEmail(userCredentials.getEmail());
        }

        // Update password if provided
        if (userCredentials.getPassword() != null && !userCredentials.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(userCredentials.getPassword());
            user.setPassword(hashedPassword);
        }

        // Save updated user to repository
        return userRepository.save(user);
    }
}
