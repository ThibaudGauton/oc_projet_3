package com.oc.projet3.rental.service;

import com.oc.projet3.rental.model.dto.RegisterRequest;
import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public AuthService(
            UserRepository userRepository,
            JWTService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    @Transactional
    public String register(RegisterRequest request) {
        // Validate if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create new User entity
        User user = new User(
                request.getName(),
                request.getEmail(),
                hashedPassword
        );

        // Save user in DB
        userRepository.save(user);

        return jwtService.generateToken(Optional.of(user));
    }
}
