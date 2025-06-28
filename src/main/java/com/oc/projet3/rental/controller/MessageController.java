package com.oc.projet3.rental.controller;

import com.oc.projet3.rental.model.dto.MessageRequest;
import com.oc.projet3.rental.model.dto.MessageResponse;
import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.service.MessageService;
import com.oc.projet3.rental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Operation(summary = "Create a new message for a rental", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody MessageRequest messageRequest) {
        Optional<User> currentUserOptional = userService.getCurrentAuthenticatedUser();
        if (currentUserOptional.isEmpty()) {
            return new ResponseEntity<>("Unauthorized: User not authenticated.", HttpStatus.UNAUTHORIZED);
        }
        Long currentAuthenticatedUserId = currentUserOptional.get().getId();

        try {
            Optional<MessageResponse> savedMessageResponse = messageService.createMessage(messageRequest, currentAuthenticatedUserId);

            if (savedMessageResponse.isPresent()) {
                return new ResponseEntity<>(savedMessageResponse.get(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Bad Request: Could not create message (e.g., rental or user not found).", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}