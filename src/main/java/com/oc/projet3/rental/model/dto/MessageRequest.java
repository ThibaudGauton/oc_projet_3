package com.oc.projet3.rental.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    private String message;

    @JsonProperty("rental_id")
    private Long rentalId;

    @JsonProperty("user_id")
    private Long userId;
}