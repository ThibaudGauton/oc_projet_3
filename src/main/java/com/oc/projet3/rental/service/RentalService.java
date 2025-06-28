package com.oc.projet3.rental.service;

import com.oc.projet3.rental.model.dto.RentalDTO;
import com.oc.projet3.rental.model.dto.RentalListResponse;
import com.oc.projet3.rental.model.dto.RentalUpdateRequest;
import com.oc.projet3.rental.model.entity.Rental;
import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentalService {
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";
    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public RentalDTO saveRentalWithImage(String name, float surface, float price, String description, MultipartFile pictureFile, User owner) throws IOException {
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rental.setOwner(owner);

        String filename = null;

        if (pictureFile != null && !pictureFile.isEmpty()) {
            filename = generateUniqueAndCleanFilename(pictureFile.getOriginalFilename());
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(pictureFile.getInputStream(), filePath);
        }

        rental.setPicture(filename);
        rental.setCreated_at(new Date());
        rental.setUpdated_at(new Date());

        Rental savedRentalInDb = rentalRepository.save(rental);

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

    public RentalListResponse getAllRentals() {
        Collection<Rental> rentals = rentalRepository.findAll();
        List<RentalDTO> rentalDTOs = rentals.stream()
                .map(this::mapToRentalDTO)
                .collect(Collectors.toList());
        for (RentalDTO rental : rentalDTOs) {
            setFullImageUrl(rental);
        }

        return new RentalListResponse(rentalDTOs);
    }

    private void setFullImageUrl(RentalDTO rental) {
        if (rental.getPicture() != null && !rental.getPicture().isEmpty()) {
            String storedFilename = rental.getPicture();
            if (!storedFilename.startsWith("http://") && !storedFilename.startsWith("https://")) {
                String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/images/")
                        .path(storedFilename)
                        .toUriString();
                rental.setPicture(imageUrl);
            }
        }
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
    public Optional<RentalDTO> updateRental(Long id, String name, float surface, float price, String description, Long currentUserId) {
        Optional<Rental> existingRentalOptional = rentalRepository.findById(id);

        if (existingRentalOptional.isEmpty()) {
            return Optional.empty();
        }

        RentalUpdateRequest updateRequest = new RentalUpdateRequest(name, surface, price, description);


        Rental existingRental = existingRentalOptional.get();

        if (!existingRental.getOwner().getId().equals(currentUserId)) {
            return Optional.empty();
        }

        existingRental.setName(updateRequest.getName());
        existingRental.setSurface(updateRequest.getSurface());
        existingRental.setPrice(updateRequest.getPrice());
        existingRental.setDescription(updateRequest.getDescription());
        existingRental.setUpdated_at(new Date());

        Rental updatedRentalInDb = rentalRepository.save(existingRental);

        return Optional.of(mapToRentalDTO(updatedRentalInDb));
    }
}
