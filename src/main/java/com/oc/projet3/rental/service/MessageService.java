package com.oc.projet3.rental.service;

import com.oc.projet3.rental.model.dto.MessageRequest;
import com.oc.projet3.rental.model.dto.MessageResponse;
import com.oc.projet3.rental.model.entity.Message;
import com.oc.projet3.rental.model.entity.Rental;
import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.repository.MessageRepository;
import com.oc.projet3.rental.repository.RentalRepository;
import com.oc.projet3.rental.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, RentalRepository rentalRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Saves a new message after performing authorization checks.
     * @param messageRequest The incoming message data from the client.
     * @param currentAuthenticatedUserId The ID of the currently authenticated user from the JWT.
     * @return An Optional containing the MessageResponse if the message was saved, otherwise empty.
     */
    public Optional<MessageResponse> saveMessage(MessageRequest messageRequest, Long currentAuthenticatedUserId) {

        if (!messageRequest.getUserId().equals(currentAuthenticatedUserId)) {
            System.err.println("Unauthorized message creation attempt: Request userId " + messageRequest.getUserId() +
                    " does not match authenticated userId " + currentAuthenticatedUserId);
            return Optional.empty();
        }

        Optional<Rental> rentalOptional = rentalRepository.findById(messageRequest.getRentalId());
        if (rentalOptional.isEmpty()) {
            System.err.println("Message creation failed: Rental with ID " + messageRequest.getRentalId() + " not found.");
            return Optional.empty();
        }
        Rental rental = rentalOptional.get();

        Optional<User> userOptional = userRepository.findById(currentAuthenticatedUserId);
        if (userOptional.isEmpty()) {
            System.err.println("Message creation failed: User with ID " + currentAuthenticatedUserId + " not found (should be authenticated).");
            return Optional.empty();
        }
        User author = userOptional.get();

        Message newMessage = new Message();
        newMessage.setMessage(messageRequest.getMessage());
        newMessage.setRental(rental);
        newMessage.setUser(author);
        newMessage.setCreated_at(new Date());

        Message savedMessage = messageRepository.save(newMessage);

        return Optional.of(mapToMessageResponse(savedMessage));
    }

    private MessageResponse mapToMessageResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setMessage(message.getMessage());
        response.setCreated_at(message.getCreated_at());
        if (message.getRental() != null) {
            response.setRentalId(message.getRental().getId());
        }
        if (message.getUser() != null) {
            response.setUserId(message.getUser().getId());
        }
        return response;
    }
}