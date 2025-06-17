package com.oc.projet3.rental.controller;

import com.oc.projet3.rental.model.dto.LightUserDTO;
import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "/users", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users")
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }

    @Operation(summary = "/user/{id}", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/user/{id}")
    public ResponseEntity<LightUserDTO> getUserById(@PathVariable Long id) {
        Optional<LightUserDTO> user = userService.getUserById(id);

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
