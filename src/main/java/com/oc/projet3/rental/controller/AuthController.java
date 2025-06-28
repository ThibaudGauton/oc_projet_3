package com.oc.projet3.rental.controller;

import com.oc.projet3.rental.model.dto.CurrentUserDTO;
import com.oc.projet3.rental.model.dto.JwtResponse;
import com.oc.projet3.rental.model.dto.LoginRequest;
import com.oc.projet3.rental.model.dto.RegisterRequest;
import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.repository.UserRepository;
import com.oc.projet3.rental.service.AuthService;
import com.oc.projet3.rental.service.JWTService;
import com.oc.projet3.rental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthController(
            AuthService authService,
            JWTService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserService userService
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            String token = authService.register(request);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during registration.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String jwt = jwtService.generateToken(user);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @Operation(summary = "/me", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            String email = jwt.getSubject();

            Optional<CurrentUserDTO> currentUserDTO = userService.getCurrentUserDetails(email);

            if (currentUserDTO.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            return ResponseEntity.ok(currentUserDTO.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }
}