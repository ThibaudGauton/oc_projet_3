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

    @Operation(summary = "", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody MessageRequest messageRequest) {
        Optional<User> currentUserOptional = userService.getCurrentAuthenticatedUser();
        if (currentUserOptional.isEmpty()) {
            return new ResponseEntity<>("Unauthorized: User not authenticated.", HttpStatus.UNAUTHORIZED);
        }
        Long currentAuthenticatedUserId = currentUserOptional.get().getId();

        Optional<MessageResponse> savedMessageResponse = messageService.saveMessage(messageRequest, currentAuthenticatedUserId);

        if (savedMessageResponse.isPresent()) {
            return new ResponseEntity<>(savedMessageResponse.get(), HttpStatus.CREATED);
        } else {
            if (!messageRequest.getUserId().equals(currentAuthenticatedUserId)) {
                return new ResponseEntity<>("Forbidden: User ID in request does not match authenticated user.", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>("Bad Request: Could not create message (e.g., rental not found).", HttpStatus.BAD_REQUEST);
        }
    }
}