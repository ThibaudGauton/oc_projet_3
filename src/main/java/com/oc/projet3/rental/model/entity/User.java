package com.oc.projet3.rental.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private Date createdAt;
    private Date updatedAt;

    public User(String name, String email, String hashedPassword) {
        this.name = name;
        this.email = email;
        this.password = hashedPassword;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public User() {
        this.name = "";
        this.email = "";
        this.password = "";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}
