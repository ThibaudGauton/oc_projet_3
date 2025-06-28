package com.oc.projet3.rental.controller;

import com.oc.projet3.rental.model.dto.RentalDTO;
import com.oc.projet3.rental.model.dto.RentalListResponse;
import com.oc.projet3.rental.model.dto.RentalUpdateRequest;
import com.oc.projet3.rental.model.entity.Rental;
import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.service.RentalService;
import com.oc.projet3.rental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;
    private final UserService userService;

    public RentalController(
            RentalService rentalService,
            UserService userService
    ) {
        this.rentalService = rentalService;
        this.userService = userService;
    }

    @Operation(summary = "", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<RentalDTO> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") float surface,
            @RequestParam("price") float price,
            @RequestParam("description") String description,
            @RequestParam("picture") MultipartFile pictureFile
    ) {
        System.out.println("Received rental creation request for name: " + name);

        try {
            Optional<User> ownerOptional = userService.getCurrentAuthenticatedUser();

            if (ownerOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            User owner = ownerOptional.get();

            RentalDTO createdRental = rentalService.saveRentalWithImage(name, surface, price, description, pictureFile, owner);

            return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
        } catch (IOException e) {
            System.err.println("Error saving rental with image: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "/{id}", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<RentalDTO> updateRental(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("surface") float surface,
            @RequestParam("price") float price,
            @RequestParam("description") String description
    ) {
        System.out.println("Received rental update request for ID: " + id);

        Optional<User> currentUserOptional = userService.getCurrentAuthenticatedUser();
        if (currentUserOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long currentUserId = currentUserOptional.get().getId();

        Optional<RentalDTO> updatedRentalDTO = rentalService.updateRental(id, name, surface, price, description, currentUserId);

        return updatedRentalDTO.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "/{id}", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        Optional<RentalDTO> rental = rentalService.getRentalById(id);

        return rental.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "/", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<RentalListResponse> getAllRentals() {
        RentalListResponse response = rentalService.getAllRentals();
        return ResponseEntity.ok(response);
    }


}
