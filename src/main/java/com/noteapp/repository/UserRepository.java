package com.noteapp.repository;

import com.noteapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     *
     * @param email The email of the user to find.
     * @return An Optional containing the user if found, empty otherwise.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the specified email exists.
     *
     * @param email The email to check for existence.
     * @return true if a user with the email exists, false otherwise.
     */
    boolean existsByEmail(@Param("email") String email);
}
