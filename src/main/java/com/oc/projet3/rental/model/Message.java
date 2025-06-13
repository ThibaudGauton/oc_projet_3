package com.oc.projet3.rental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Message {
    @Getter
    @Id
    private Long id;
    @Setter
    @Getter
    private String message;
    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;
}
