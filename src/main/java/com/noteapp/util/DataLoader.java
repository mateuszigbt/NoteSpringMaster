package com.noteapp.util;

import com.noteapp.entity.User;
import com.noteapp.entity.auth.Role;
import com.noteapp.repository.UserRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public DataLoader(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        loadCSVData();
    }

    private void loadCSVData() {
        try (CSVReader reader = new CSVReader(new FileReader("src/main/resources/users.csv"))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                String email = line[0];
                String password = passwordEncoder.encode(line[1]);
                Set<Role> roles = new HashSet<>();
                Arrays.stream(line[2].split(",")).forEach(role -> roles.add(Role.valueOf(role.trim())));

                User user = new User(email, password, roles);
                userRepository.save(user);
            }
        } catch (IOException | CsvValidationException e) {
            log.debug(e.getMessage());
        }
    }
}