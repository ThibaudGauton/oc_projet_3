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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Optional<MessageResponse> createMessage(MessageRequest messageRequest, Long authenticatedUserId) throws Exception, Exception {
        if (!messageRequest.getUserId().equals(authenticatedUserId)) {
            throw new Exception("User ID in request does not match authenticated user. Cannot create message on behalf of another user.");
        }

        Optional<User> senderOptional = userRepository.findById(authenticatedUserId);
        Optional<Rental> rentalOptional = rentalRepository.findById(messageRequest.getRentalId());

        if (senderOptional.isEmpty() || rentalOptional.isEmpty()) {

            return Optional.empty();
        }

        User sender = senderOptional.get();
        Rental rental = rentalOptional.get();

        Message newMessage = new Message();
        newMessage.setMessage(messageRequest.getMessage());
        newMessage.setRental(rental);
        newMessage.setUser(sender);
        newMessage.setCreated_at(new Date());

        Message savedMessage = messageRepository.save(newMessage);

        MessageResponse response = mapToMessageResponse(savedMessage);

        return Optional.of(response);
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