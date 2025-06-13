package com.oc.projet3.rental.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Rental {
    @Getter
    @Id
    private Long id;
    @Setter
    private String name;
    @Setter
    private float price;
    @Setter
    private float surface;
    @Setter
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Setter
    private Date created_at;
    @Setter
    private Date updated_at;
    @Setter
    private String description;
    @Setter
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
