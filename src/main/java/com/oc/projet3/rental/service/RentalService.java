package com.oc.projet3.rental.service;

import com.oc.projet3.rental.model.dto.RentalDTO;
import com.oc.projet3.rental.model.dto.RentalUpdateRequest;
import com.oc.projet3.rental.model.entity.Rental;
import com.oc.projet3.rental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalService {
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";
    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public RentalDTO saveRentalWithImage(Rental rental, MultipartFile pictureFile) throws IOException {
        String filename = null; // Initialize filename to null

        if (pictureFile != null && !pictureFile.isEmpty()) {
            // 1. Generate a unique and clean filename
            filename = generateUniqueAndCleanFilename(pictureFile.getOriginalFilename());
            Path uploadPath = Paths.get(UPLOAD_DIR);

            // Create the directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the file to the specified directory
            Path filePath = uploadPath.resolve(filename);
            Files.copy(pictureFile.getInputStream(), filePath);
        }

        // Set the picture filename in the rental object (will be null if no file uploaded)
        rental.setPicture(filename);
        rental.setCreated_at(new Date()); // Set creation date
        rental.setUpdated_at(new Date()); // Set update date

        Rental savedRentalInDb = rentalRepository.save(rental);

        // Save the rental object to the database
        return mapToRentalDTO(savedRentalInDb);
    }

    private String generateUniqueAndCleanFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return UUID.randomUUID().toString() + ".png";
        }

        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            fileExtension = originalFilename.substring(dotIndex);
        }

        String baseName = (dotIndex > 0) ? originalFilename.substring(0, dotIndex) : originalFilename;
        String cleanedBaseName = baseName.replaceAll("[^a-zA-Z0-9\\-_.]", "").replaceAll("\\s+", "-");

        String uniqueId = String.valueOf(System.currentTimeMillis());

        if (cleanedBaseName.isEmpty()) {
            cleanedBaseName = "file";
        }

        return uniqueId + "_" + cleanedBaseName + fileExtension;
    }

    public Optional<RentalDTO> getRentalById(Long id) {
        return rentalRepository.findById(id)
                .map(this::mapToRentalDTO);
    }

    public Iterable<RentalDTO> getAllRentals() {
        Collection<Rental> rentals = rentalRepository.findAll();
        // Convert the Iterable of Rental entities to a List of RentalDTOs
        return rentals.stream()
                .map(this::mapToRentalDTO)
                .collect(Collectors.toList());
    }

    private RentalDTO mapToRentalDTO(Rental rental) {
        RentalDTO dto = new RentalDTO();
        dto.setId(rental.getId());
        dto.setName(rental.getName());
        dto.setSurface(rental.getSurface());
        dto.setPrice(rental.getPrice());
        dto.setDescription(rental.getDescription());
        dto.setCreated_at(rental.getCreated_at());
        dto.setUpdated_at(rental.getUpdated_at());

        if (rental.getOwner() != null) {
            dto.setOwner_id(rental.getOwner().getId());
        }

        if (rental.getPicture() != null && !rental.getPicture().isEmpty()) {
            String storedFilename = rental.getPicture();
            if (!storedFilename.startsWith("http://") && !storedFilename.startsWith("https://")) {
                String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/images/")
                        .path(storedFilename)
                        .toUriString();
                dto.setPicture(imageUrl);
            } else {
                dto.setPicture(storedFilename);
            }
        } else {
            dto.setPicture(null);
        }
        return dto;
    }

    /**
     * Updates an existing rental.
     * @param id The ID of the rental to update.
     * @param updateRequest The DTO containing updated rental details.
     * @param currentUserId The ID of the currently authenticated user (for authorization).
     * @return An Optional containing the updated RentalDTO if successful, otherwise empty.
     */
    public Optional<RentalDTO> updateRental(Long id, RentalUpdateRequest updateRequest, Long currentUserId) {
        Optional<Rental> existingRentalOptional = rentalRepository.findById(id);

        if (existingRentalOptional.isEmpty()) {
            return Optional.empty(); // Rental not found
        }

        Rental existingRental = existingRentalOptional.get();

        // Authorization check: Only the owner can update their rental
        if (!existingRental.getOwner().getId().equals(currentUserId)) {
            return Optional.empty();
        }

        // Apply updates from the DTO to the existing entity
        existingRental.setName(updateRequest.getName());
        existingRental.setSurface(updateRequest.getSurface());
        existingRental.setPrice(updateRequest.getPrice());
        existingRental.setDescription(updateRequest.getDescription());
        existingRental.setUpdated_at(new Date()); // Update timestamp

        // Save the updated rental (will perform an UPDATE operation)
        Rental updatedRentalInDb = rentalRepository.save(existingRental);

        // Map and return the updated DTO with the full image URL
        return Optional.of(mapToRentalDTO(updatedRentalInDb));
    }
}
