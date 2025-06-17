package com.oc.projet3.rental.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String message;

    @JsonProperty("rental_id")
    private Long rentalId;

    @JsonProperty("author_id")
    private Long userId;

    private Date created_at;
}