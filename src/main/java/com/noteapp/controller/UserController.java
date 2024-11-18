package com.noteapp.controller;

import com.noteapp.dto.auth.UserCredentials;
import com.noteapp.entity.User;
import com.noteapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class handling user-related HTTP requests.
 * Endpoints for user management, such as fetching, updating, and deleting users, are provided.
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Constructor for UserController, injecting an instance of UserService.
     * @param userService The service responsible for user-related operations.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for fetching all users. Requires ADMIN role.
     * @return ResponseEntity containing a list of User objects representing all users.
     */
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint for fetching the currently logged-in user. Requires USER role.
     * @return ResponseEntity containing the User object representing the current user.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint for fetching a user by their ID.
     * @param id The ID of the user to be fetched.
     * @return ResponseEntity containing the User object with the specified ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint for fetching a user by their email address.
     * @param email The email address of the user to be fetched.
     * @return ResponseEntity containing the User object with the specified email address.
     */
    @GetMapping("")
    public ResponseEntity<User> getUserByEmail(@RequestParam("email") String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint for updating current user's information.
     * @param userCredentials UserCredentials object containing updated user details.
     * @return ResponseEntity containing the updated User object.
     */
    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(@RequestBody UserCredentials userCredentials) {
        User updatedUser = userService.updateCurrentUser(userCredentials);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint for deleting a user by their ID.
     * @param id The ID of the user to be deleted.
     * @return ResponseEntity indicating successful deletion with no content.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}