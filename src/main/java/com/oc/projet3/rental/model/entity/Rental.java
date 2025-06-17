package com.oc.projet3.rental.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
public class Rental {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private float price;
    private float surface;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    private Date created_at;
    private Date updated_at;
    private String description;
    private String picture;
    @OneToMany(mappedBy = "rental")
    private final List<Message> messages;

    public Rental() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
    }
}
